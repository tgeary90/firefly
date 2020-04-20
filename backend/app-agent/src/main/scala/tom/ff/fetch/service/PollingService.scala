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

  type ResultsWrapper = Seq[Seq[Result[FetchError, Transaction]]]
  type JobsWrapper = Seq[Result[JobError, List[Job[Transaction]]]]

  val dateFormat    = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger   = LoggerFactory.getLogger("PollingService")

  log.info(s"Polling interval set at ${pollingInterval} ms")

  @Scheduled(fixedRateString = "${polling.interval}")
  def start(): Unit = {
    log.debug("Getting transactions at: " + dateFormat.format(new Date))

    val connectors: Seq[Connector]  = registrationService.getConnectors

    val results: ResultsWrapper = for {
      conn <- connectors
    } yield Workflows.fetch(conn)

    val batch: JobsWrapper = results.flatMap {
      resultsPerConnector =>
        val txns = resultsPerConnector.flatMap { res =>
          res.result match {
            case Right(txn) => List(txn)
          }
        }
        Workflows.createJob(txns.toList)
      }

    batch.foreach { txn => Workflows.enqueue(queueClient, txn) }
  }

  def stop(): Unit = {
    // TODO stop service on shutdown
  }
}
