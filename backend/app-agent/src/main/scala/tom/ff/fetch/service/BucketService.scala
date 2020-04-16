package tom.ff.fetch.service

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.Bucket

import scala.collection.mutable.ArrayBuffer

@Component
class BucketService {

  private val buckets = new ArrayBuffer[Bucket]
  private var bucketCounter = 0

  def getBuckets: Seq[Bucket] = buckets.toList

  def deleteBucket(id: Int): Unit = {
    val b  = new Bucket(id, "")
    if (buckets.contains(b)) buckets -= b
  }

  def addBucket(bucketUrl: String): Unit = {
    val b = new Bucket(bucketCounter, bucketUrl)
    if ( ! buckets.contains(b)) buckets += (b)
    bucketCounter += 1
  }
}
