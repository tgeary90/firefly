package tom.ff.fetch.service

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.FetchTypes.Connector

import scala.collection.mutable.ArrayBuffer

@Component
class RegistrationService() {
  private val connectors = new ArrayBuffer[Connector]()

  def getConnectors: List[Connector] = connectors.toList
  def getConnector(providerName: String): Connector = connectors.filter(c => c.getProviderName() == providerName)(0)
  def addConnector(cloudAgent: Connector): Unit = connectors += cloudAgent
}
