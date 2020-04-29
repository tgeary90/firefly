package tom.ff.fetch.service

import java.text.SimpleDateFormat
import java.util.Date

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.FetchTypes._
import tom.ff.fetch.domain.FetchWorkflows

@Component
class PollingService(
                      registrationService: RegistrationService,
                      bucketService: BucketService,
                      queueClient: QueueClient,
                      @Value("${polling.interval}") pollingInterval: String,
                      fileTable: FileTable
                    ) {

  type AggregateResults = Seq[Option[Seq[Result[FetchError, RawTransaction]]]]
  type Batch = Seq[Option[Result[JobError, Job[RawTransaction]]]]

  val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger   = LoggerFactory.getLogger("PollingService")

  log.info(s"Polling interval set at ${pollingInterval} ms")

  @Scheduled(fixedRateString = "${polling.interval}")
  def start(): Unit = {
    log.debug("Getting transactions at: " + dateFormat.format(new Date))

    val connectors: Seq[Connector]  = registrationService.getConnectors

    val aggregateResults: AggregateResults = for {
      conn <- connectors
    } yield FetchWorkflows.fetch(conn, fileTable)

    val batches: Batch = aggregateResults.map {
      maybeResults => {
        maybeResults.map {
          maybeResultsPerConnector => {
            val txns = maybeResultsPerConnector.flatMap {
              res =>
                List(res.result.right.toOption)
            }.flatten.toList
            val job = FetchWorkflows.createJob(connectors(0), txns) // TODO only  works when theres one connector
            job
          }
        }
      }
    }

    batches.map { maybeBatch =>
      maybeBatch.map { batch =>
          FetchWorkflows.enqueue(queueClient, batch.result.right.get)
        }
      }
  }

  def stop(): Unit = {
    // TODO stop service on shutdown
  }
}