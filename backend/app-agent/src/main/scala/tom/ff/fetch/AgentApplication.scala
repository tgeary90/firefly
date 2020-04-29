package tom.ff.fetch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}
import org.springframework.scheduling.annotation.EnableScheduling
import tom.ff.fetch.domain.FetchTypes.{Connector, FileName, Provider}
import tom.ff.fetch.service.RegistrationService
import tom.ff.gcp.agent.GCPConnector

import scala.collection.mutable.{Map => MMap}

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
  def fileTable: MMap[Provider, Set[FileName]] = MMap.empty[Provider, Set[FileName]]

  @Bean
  def gcpAgent(gcpAgent: GCPConnector, registrationService: RegistrationService): Connector = {
    val connector = new Connector {
      override def getObjects(): Seq[(String, Any)] = gcpAgent.getObjects()
      override def getProviderName(): String = "gcp"
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
