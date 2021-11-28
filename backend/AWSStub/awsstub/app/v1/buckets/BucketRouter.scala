package v1.buckets

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import javax.inject.Inject

class BucketRouter @Inject()(controller: BucketController) extends SimpleRouter {

  val prefix = "/v1/buckets"

  def link(id: BucketId): String = {
    import io.lemonlabs.uri.dsl._
    val url = prefix / id
    url
  }

  override def routes: Routes = {
    case GET(p"/count") => controller.bucketCount
    case GET(p"/$id") => controller.bucketContents(id)
    case GET(p"/") => controller.list
  }
}
