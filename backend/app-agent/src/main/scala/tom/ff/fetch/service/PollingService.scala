package tom.ff.fetch.service

import java.text.SimpleDateFormat
import java.util.Date

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types._
import tom.ff.fetch.domain.Workflows

@Component
class PollingService(
                      registrationService: RegistrationService,
                      bucketService: BucketService,
                      queueClient: QueueClient,
                      @Value("${polling.interval}") pollingInterval: String
                    ) {

  val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger   = LoggerFactory.getLogger("PollingService")

  log.info(s"Polling interval set at ${pollingInterval} ms")

  @Scheduled(fixedRateString = "${polling.interval}")
  def start(): Unit = {
    log.debug("Getting transactions at: " + dateFormat.format(new Date))

    val connectors: Seq[Connector]  = registrationService.getConnectors
    val buckets: Seq[Bucket]        = bucketService.getBuckets

    val results: Seq[Result[FetchError, List[Transaction]]] = for {
      conn <- connectors
      bucket <- buckets
    } yield Workflows.fetch(conn, bucket)

    val batch: Seq[Result[JobError, List[Job[Transaction]]]] = results.map {r =>
      Workflows.createJob(r.result match {
        case Right(txns) => txns
        case Left(e) => throw new JobError(e.getMessage)
      })
    }

    batch.foreach { r =>
      Workflows.enqueue(queueClient, r.result.right.get)
    }
  }

  def stop(): Unit = {
    // TODO stop service on shutdown
  }
}
