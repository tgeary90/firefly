package v1.buckets

import net.logstash.logback.marker.LogstashMarker
import play.api.MarkerContext
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{BaseController, ControllerComponents, DefaultActionBuilder, PlayBodyParsers, RequestHeader}

import javax.inject.Inject

/**
* Provides an implicit marker that will show the request in all logger statements.
*/
trait RequestMarkerContext {
  import net.logstash.logback.marker.Markers

  private def marker(tuple: (String, Any)) = Markers.append(tuple._1, tuple._2)

  private implicit class RichLogstashMarker(marker1: LogstashMarker) {
    def &&(marker2: LogstashMarker): LogstashMarker = marker1.and(marker2)
  }

  implicit def requestHeaderToMarkerContext(
                                             implicit request: RequestHeader): MarkerContext = {
    MarkerContext {
      marker("id" -> request.id) && marker("host" -> request.host) && marker(
        "remoteAddress" -> request.remoteAddress)
    }
  }

}

// sugar so that only one class need be injected into controller
case class BucketControllerComponents @Inject()(
                                               bucketActionBuilder: BucketActionBuilder,
                                               bucketResourceHandler: BucketResourceHandler,
                                               actionBuilder: DefaultActionBuilder,
                                               parsers: PlayBodyParsers,
                                               messsageApi: MessagesApi,
                                               langs: Langs,
                                               fileMimeTypes: FileMimeTypes,
                                               executionContext: scala.concurrent.ExecutionContext
                                               ) extends ControllerComponents {
  override def messagesApi: MessagesApi = ???
}

class BucketBaseController @Inject()(bcc: BucketControllerComponents) extends BaseController with RequestMarkerContext {

  override protected def controllerComponents: ControllerComponents = bcc

  def BucketAction: BucketActionBuilder = bcc.bucketActionBuilder

  def bucketResourceHandler: BucketResourceHandler = bcc.bucketResourceHandler


}
