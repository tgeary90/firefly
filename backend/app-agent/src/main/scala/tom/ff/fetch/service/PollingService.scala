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

  val dateFormat            = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss")
  val log: Logger           = LoggerFactory.getLogger("PollingService")

  log.info(s"Polling interval set at ${pollingInterval} ms")

  @Scheduled(fixedRateString = "${polling.interval}")
  def start(): Unit = {
    log.debug("Getting transactions at: " + dateFormat.format(new Date))

    val connectors: Seq[Connector]  = registrationService.getConnectors

    val ts =   Seq(
      RawTransaction(
        Originator("mickey", AccountNumber(12345678)),
        Beneficiary("mallory", AccountNumber(87654321)),
        Money(10.0, "stirling"),
        Debit()
      ),
      RawTransaction(
        Originator("han sole", AccountNumber(12345633)),
        Beneficiary("luke skywalker", AccountNumber(87654344)),
        Money(20.0, "creds"),
        Credit()
      )
    )

    for (connector <- connectors) {
      val acknowledgements = for {
        responsesForThatCloud <- FetchWorkflows.fetchObjects(connector, fileTable)
        job                   <- FetchWorkflows.createJob(connector, responsesForThatCloud)
        ack                   <- FetchWorkflows.enqueue(queueClient, job)
      } yield ack

      acknowledgements.map(ack => log.debug(s"${connector.getProviderName()} sent ${ack}"))
    }

  }

  def stop(): Unit = {
    // TODO stop service on shutdown
  }
}