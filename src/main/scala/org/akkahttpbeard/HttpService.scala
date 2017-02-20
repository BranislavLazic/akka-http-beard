/*
 * Copyright 2017 Branislav Lazic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akkahttpbeard

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentType, HttpEntity}
import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.server.Route
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import de.zalando.beard.renderer._
import akka.http.scaladsl.model.MediaTypes.`text/html`
import akka.http.scaladsl.server.Directives._
import scala.util.Failure

object HttpService {

  final val Name = "http-service"
  final case class President(firstName: String, lastName: String)

  val templateLoader = new ClasspathTemplateLoader(
    templatePrefix = "/web/",
    templateSuffix = ".html"
  )
  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = templateLoader)
  val templateRenderer = new BeardTemplateRenderer(templateCompiler)

  def renderPage(templateName: String, context: Map[String, Any]): Route = {
    val template = templateCompiler.compile(TemplateName(templateName)).get
    val result =
      templateRenderer.render(template, StringWriterRenderResult(), context, Some(template))
    complete(HttpEntity(ContentType(`text/html`, `UTF-8`), result.toString))
  }

  def route: Route =
    getFromResourceDirectory("web") ~ pathSingleSlash {
      get {
        val context: Map[String, Any] = Map(
          "title" -> "US presidents",
          "presidents" -> List(
            Map("number" -> "45", "firstName" -> "Donald", "lastName" -> "Trump"),
            Map("number" -> "44", "firstName" -> "Barack", "lastName" -> "Obama"),
            Map("number" -> "43", "firstName" -> "George", "lastName" -> "Bush Jr."),
            Map("number" -> "42", "firstName" -> "Bill", "lastName"   -> "Clinton"),
            Map("number" -> "41", "firstName" -> "George", "lastName" -> "Bush")
          )
        )
        renderPage("index", context)
      }
    }

  def apply(address: String, port: Int): Props = Props(new HttpService(address, port))

}

final class HttpService(address: String, port: Int) extends Actor with ActorLogging {
  import HttpService._
  import context.dispatcher
  implicit val mat = ActorMaterializer()

  Http(context.system).bindAndHandle(route, address, port).pipeTo(self)

  override def receive: Receive = {
    case ServerBinding(serverAddress) =>
      log.info(
        s"Server is successfully listening at: ${serverAddress.getHostName}:${serverAddress.getPort}"
      )
    case Failure(cause) => log.error(cause, "Failed to start server!")
  }
}
