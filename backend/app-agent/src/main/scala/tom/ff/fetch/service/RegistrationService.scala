package tom.ff.fetch.service

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.Connector

import scala.collection.mutable.ArrayBuffer

@Component
class RegistrationService() {
  private val connectors = new ArrayBuffer[Connector]()

  def getConnectors: List[Connector] = connectors.toList

  def addConnector(cloudAgent: Connector): Unit = connectors += cloudAgent
}
