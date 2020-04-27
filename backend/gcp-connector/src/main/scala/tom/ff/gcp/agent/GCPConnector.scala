package tom.ff.gcp.agent

import java.io.FileInputStream

import com.google.api.gax.paging.Page
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage.BucketListOption
import com.google.cloud.storage.{Blob, Bucket, StorageOptions}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import scala.collection.Iterable
import scala.collection.JavaConversions._

@Component
class GCPConnector(@Value("${gcp.svc.account.file}") serviceAccountFile: String) {

  val log: Logger = LoggerFactory.getLogger("GCPConnector")
  log.debug(s"Reading Service Account File at: $serviceAccountFile")

  val storageOptions = StorageOptions.newBuilder
    .setProjectId("the-dock-259022")
    .setCredentials(GoogleCredentials.fromStream(
      new FileInputStream(serviceAccountFile))).build
  val storage = storageOptions.getService

  def getObjects(): Seq[(String, Any)] = {
    val buckets: Page[Bucket] = storage.list(BucketListOption.pageSize(100))
    val bucketList: List[Bucket] = buckets.iterateAll().toList
    log.debug(s"Found ${bucketList.size} buckets")

    val pages: Seq[Page[Blob]] = for {
      bucket <- bucketList
    } yield bucket.list()

    val blobsList: Seq[Iterable[Blob]] = for {
      page <- pages
    } yield page.iterateAll().toSeq

    val fileList: Seq[(String, Array[Byte])] = for {
      blobs <- blobsList
      blob <- blobs
    } yield (blob.getName, blob.getContent())

    log.debug(s"Returned ${fileList.size} objects from GCP buckets")
    fileList
  }
}