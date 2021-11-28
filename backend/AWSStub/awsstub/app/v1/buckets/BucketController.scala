package v1.buckets

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BucketController @Inject()(cc: BucketControllerComponents)(implicit ec: ExecutionContext) extends BucketBaseController(cc) {

  private val logger = Logger(getClass)

  // custom action builders

  def list: Action[AnyContent] = BucketAction.async { implicit request =>
    logger.info("index: ")
    bucketResourceHandler.find.map { buckets =>
      Ok(Json.toJson(buckets))
    }
  }

  def bucketContents(id: String): Action[AnyContent] = BucketAction.async { implicit request =>
    logger.info(s"contents for $id")
    bucketResourceHandler.lookup(id).map { bucket =>
      Ok(Json.toJson(bucket)) // returns a BucketResource and serializes as status 200 Result
    }
  }

  def bucketCount: Action[AnyContent] = BucketAction.async { implicit request =>
    logger.info("Count is ...")
    bucketResourceHandler.find.map { buckets =>
      Ok(Json.toJson(buckets.get.size))}
  }
}
