package tom.ff.gcp.agent

import org.springframework.stereotype.Component
import com.google.api.gax.paging.Page
import com.google.cloud.storage.Storage.BucketListOption
import com.google.cloud.storage.{Blob, Bucket, Storage, StorageOptions}
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.StorageOptions
import java.io.FileInputStream

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

@Component
class GCPConnector(@Value("${gcp.svc.account.file}") serviceAccountFile: String) {

  val log: Logger         = LoggerFactory.getLogger("GCPConnector")
  log.info(s"Reading Service Account File at: $serviceAccountFile")

  val storageOptions = StorageOptions.newBuilder
    .setProjectId("the-dock-259022")
    .setCredentials(GoogleCredentials.fromStream(
      new FileInputStream(serviceAccountFile))).build
  val storage = storageOptions.getService

  def getObjects(): Seq[Any] = {
    val buckets: Page[Bucket] = storage.list(BucketListOption.pageSize(100))
    val bucketList: List[Bucket] = buckets.iterateAll().toList
    log.info(s"Found ${bucketList.size} buckets")

    val objectsBuffer = new ArrayBuffer[Any]()
    val pages: Seq[Page[Blob]] = for {
      bucket <- bucketList
    } yield bucket.list()

    val blobsList: Seq[Iterable[Blob]] = for {
      page <- pages
    } yield page.iterateAll().toSeq

    val objectsList: Seq[Iterable[Array[Byte]]] = for {
      blobs <- blobsList
    } yield blobs.map(b => b.getContent())

    for {
      objects <- objectsList
      bytes <- objects
    } yield objectsBuffer += (bytes)
    log.info(s"Found ${objectsBuffer.size} objects")

    objectsBuffer.toSeq
  }
}