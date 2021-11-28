package v1.buckets

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class BucketId private (value: Int)

object BucketId {
  def apply(raw: String): BucketId = {
    require(raw != null)
    new BucketId(Integer.parseInt(raw))
  }
}

final case class BucketData(id: BucketId, contents: List[String])

trait BucketRepository {
  def find(): Future[Option[List[BucketData]]]
  def lookup(id: String): Future[Option[BucketData]]
}

//@Singleton
class HardcodedBucketRepository @Inject()(implicit ec: ExecutionContext) extends BucketRepository {

  private val buckets = List(
    BucketData(BucketId(1), List("some data")),
    BucketData(BucketId(2), List("some data")),
    BucketData(BucketId(3), List("some data"))
  )

  override def find(): Future[Option[List[BucketData]]] = Future {
    Option(buckets)
  }

  override def lookup(id: String): Future[Option[BucketData]] = Future {
    buckets.filter(b => b.id.value.toString == id) match {
//      case ls @ List(_) => Some(ls(0))
      case h :: t => Some(h)
      case _ => None
    }
  }
}

