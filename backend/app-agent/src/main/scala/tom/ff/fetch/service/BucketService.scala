package tom.ff.fetch.service

import java.sql.Date
import java.text.SimpleDateFormat

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.Bucket

import scala.collection.mutable.ArrayBuffer

@Component
class BucketService {

  private val buckets       = new ArrayBuffer[Bucket]
  private var bucketCounter = 0
  private val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")

  def getBuckets: Seq[Bucket] = buckets.toList

  def deleteBucket(id: Int): Unit = {
    val b  = new Bucket(id, "", 0, null)
    if (buckets.contains(b)) buckets -= b
  }

  def addBucket(bucketUrl: String, count: Int): Unit = {
    val b = new Bucket(bucketCounter, bucketUrl, count, new java.sql.Date(System.currentTimeMillis()))
    if ( ! buckets.contains(b)) buckets += (b)
    bucketCounter += 1
  }
}
