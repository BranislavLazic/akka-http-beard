import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.model.MediaTypes.`text/html`
import akka.http.scaladsl.model.{ContentType, StatusCodes}
import akka.http.scaladsl.model.headers.`Content-Type`
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class HttpServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {
  import org.akkahttpbeard.HttpService._

  "HttpService" should {
    "return rendered page upon sending GET /" in {
      Get("/") ~> route ~> check {
        status shouldBe StatusCodes.OK
        header[`Content-Type`] shouldBe Some(`Content-Type`.apply(ContentType(`text/html`, `UTF-8`)))
      }
    }
  }
}
