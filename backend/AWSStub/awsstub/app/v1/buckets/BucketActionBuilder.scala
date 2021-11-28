package v1.buckets

import play.api.MarkerContext
import play.api.http.HttpVerbs
import play.api.i18n.MessagesApi
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, MessagesRequestHeader, PlayBodyParsers, PreferredMessagesProvider, Request, Result, WrappedRequest}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


trait BucketRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider {
  // def ... contextual data
}

//wrapped request
// add security creds here
class BucketRequest[A](request: Request[A], val messagesApi: MessagesApi)
  extends WrappedRequest(request)
  with BucketRequestHeader

// this build the BucketAction.async function.
// put logging, metrics, other processing here.
class BucketActionBuilder @Inject()(
                                     messagesApi: MessagesApi,
                                     playBodyParsers: PlayBodyParsers
                                   )
                                   (implicit val executionContext: ExecutionContext)
                                   extends ActionBuilder[BucketRequest, AnyContent]
                                   with RequestMarkerContext
                                   with HttpVerbs {

  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type BucketRequestBlock[A] = BucketRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A], block: BucketRequest[A] => Future[Result]): Future[Result] = {

    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(request)
    val fut = block(new BucketRequest(request, messagesApi))

    fut.map {
      result => request.method match {
        case GET | HEAD => result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other => result
      }
    }
  }
}
