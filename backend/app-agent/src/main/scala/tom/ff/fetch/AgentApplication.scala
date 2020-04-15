package tom.ff.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.web.context.WebApplicationContext
import tom.ff.fetch.domain.Types.{CloudAgent, CloudService}
import tom.ff.fetch.service.PollingService
import tom.ff.gcp.agent.GCPAgent

import scala.beans.BeanProperty

@SpringBootApplication
@ComponentScan(
    basePackages = Array(
      "tom.ff.fetch",
      "tom.ff.gcp.agent"
    )
)
class AgentApp() {
  @Bean
  def agent(gcpAgent: GCPAgent): CloudAgent = new CloudAgent {
    override def fetchTransactions(bucket: String): List[Any] = gcpAgent.fetchTransactions(bucket)
  }
}

object AgentApplication {
  def main(args: Array[String]) {
    SpringApplication.run(classOf[AgentApp])
  }
}
