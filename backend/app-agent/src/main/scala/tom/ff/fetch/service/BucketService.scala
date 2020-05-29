package tom.ff.fetch.service

import java.sql.Date
import java.text.SimpleDateFormat

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.FetchTypes._

import scala.collection.mutable.ArrayBuffer

@Component
class BucketService extends BucketMetadata {

  private val buckets       = new ArrayBuffer[Bucket]
  private val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")

  def getBucketsFor(provider: String): Seq[Bucket] = buckets.filter(b => b.provider == provider)

  def updateBucketETLMetadata(bucketName: String, count: Int, provider: Provider): Unit = {
    val bucketsThatMatch = buckets.filter(b => b.name == bucketName)

    if (bucketsThatMatch.size > 0) {
      buckets.remove(buckets.indexWhere(b => b.name == bucketName))
    }
    buckets.append(Bucket(bucketName, count, new Date(System.currentTimeMillis()), provider))
  }
}
