package tom.ff.fetch.service

import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.CloudAgent

import scala.collection.mutable.ArrayBuffer

@Component
class RegistrationService() {
  private val connectors = new ArrayBuffer[CloudAgent]()

  def getConnectors: List[CloudAgent] = connectors.toList

  def addConnector(cloudAgent: CloudAgent): Unit = connectors += cloudAgent
}
