package tom.ff.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}
import tom.ff.fetch.domain.Types.{CloudAgent, CloudService}
import tom.ff.fetch.service.PollingService
import tom.ff.gcp.agent.GCPAgent

@SpringBootApplication
@ComponentScan(
    basePackages = Array(
      "tom.ff.fetch",
      "tom.ff.gcp.agent"
    )
)
class AppConfig() {

  @Bean
  def agent(gcpAgent: GCPAgent): CloudAgent = new CloudAgent {
    override def fetchTransactions(bucket: String): List[Any] = gcpAgent.fetchTransactions(bucket)
  }

  @Bean
  def poller(pollingService: PollingService, agent: CloudAgent): CloudService = new CloudService {
    override def start(cloudAgent: CloudAgent): Unit = pollingService.start(agent)
  }

  @Autowired
  var poller: CloudService = _

  @Autowired
  var agent: CloudAgent = _

  poller.start(agent)
}

object AgentApplication extends App {
    SpringApplication.run(classOf[AppConfig])

}
