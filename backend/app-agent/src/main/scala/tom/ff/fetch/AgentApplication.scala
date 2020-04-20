package tom.ff.fetch

import org.omg.IOP.TransactionService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.scheduling.annotation.EnableScheduling
import tom.ff.fetch.domain.Types.{Connector, Transaction}
import tom.ff.fetch.service.RegistrationService
import tom.ff.gcp.agent.GCPConnector

@SpringBootApplication
@ComponentScan(
    basePackages = Array(
      "tom.ff.fetch",
      "tom.ff.gcp.agent"
    )
)
@EnableScheduling
class AgentApp() {

  @Bean
  def gcpAgent(gcpAgent: GCPConnector, registrationService: RegistrationService): Connector = {
    val connector = new Connector {
      override def getObjects(): Seq[Any] = gcpAgent.getObjects()
    }
    registrationService.addConnector(connector)
    connector
  }

  // Add more connectors as necessary

}

object AgentApplication {
  def main(args: Array[String]) {
    SpringApplication.run(classOf[AgentApp])
  }
}
