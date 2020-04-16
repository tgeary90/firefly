package tom.ff.fetch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.scheduling.annotation.EnableScheduling
import tom.ff.fetch.domain.Types.CloudAgent
import tom.ff.fetch.service.RegistrationService
import tom.ff.gcp.agent.GCPAgent

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
  def gcpAgent(gcpAgent: GCPAgent, registrationService: RegistrationService): CloudAgent = {
    val connector = new CloudAgent {
      override def fetchTransactions(bucket: String): List[Any] = gcpAgent.fetchTransactions(bucket)
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
