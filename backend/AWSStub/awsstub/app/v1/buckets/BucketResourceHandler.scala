package v1.buckets

import play.api.libs.json.{Format, Json}

import javax.inject.{Inject, Provider}
import scala.concurrent.{ExecutionContext, Future}

// base DTO
case class BucketResource(id: String, provider: String, contents: List[String])

object BucketResource {
  implicit val format: Format[BucketResource] = Json.format
}

class BucketResourceHandler @Inject()(
                             routerProvider: Provider[BucketRouter],
                             bucketRepository: BucketRepository
                             )
                             (implicit ec: ExecutionContext) {

  def lookup(id: String): Future[Option[BucketResource]] = bucketRepository.lookup(id).map(o => o.map(b => BucketResource(b.id.value.toString, "aws", b.contents)))

  def find: Future[Option[List[BucketResource]]] = bucketRepository.find.map(o => o.map(ls => ls.map(b => BucketResource(b.id.value.toString, "aws", b.contents))))
}
