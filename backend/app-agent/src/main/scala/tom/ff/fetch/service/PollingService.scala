package tom.ff.fetch.service

import java.text.SimpleDateFormat
import java.util.Date

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.{Bucket, CloudAgent}

@Component
class PollingService(
                      registrationService: RegistrationService,
                      bucketService: BucketService,
                      @Value("${polling.interval}") pollingInterval: String
                    ) {

  val dateFormat = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger = LoggerFactory.getLogger("PollingService")

  @Scheduled(fixedRateString = "${polling.interval}")
  def start(): Unit = {

    log.debug("Getting transactions at: " + dateFormat.format(new Date))

    val connectors: Seq[CloudAgent] = registrationService.getConnectors
    val buckets: Seq[Bucket] = bucketService.getBuckets

    val objects = for {
      conn <- connectors
      bucket <- buckets
    } yield conn.fetchTransactions(bucket.url)

    // TODO make transactions out of the objects

    // TODO serialize them to binary

    // TODO drop on RMQ
  }

  def stop(): Unit = {
    // TODO stop service on shutdown
  }
}
