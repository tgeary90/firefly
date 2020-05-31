package tom.ff.fetch.service

import java.sql.Date
import java.text.SimpleDateFormat

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.FetchTypes._

import scala.collection.mutable.ArrayBuffer

@Component
class BucketService extends BucketMetadata {

  private val buckets       = new ArrayBuffer[Bucket]
  private val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger           = LoggerFactory.getLogger("BucketService")

  def getBucketsFor(provider: String): Seq[Bucket] = {
    val bucketsForProvider = buckets.filter(b => b.provider == provider)
    log.info(s"${bucketsForProvider.size} buckets returned for ${provider}")
    bucketsForProvider
  }

  def updateBucketETLMetadata(bucketName: String, count: Int, provider: Provider): Unit = {
    val bucketsThatMatch = buckets.filter(b => b.name == bucketName)

    if (bucketsThatMatch.size > 0) {
      buckets.remove(buckets.indexWhere(b => b.name == bucketName))
    }
    val newBucket = Bucket(bucketName, count, new Date(System.currentTimeMillis()), provider)
    log.debug(s"metadata updated for... \n\t${newBucket}")
    buckets.append(newBucket)
  }
}
