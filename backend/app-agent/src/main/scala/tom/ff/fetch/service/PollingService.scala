package tom.ff.fetch.service

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.Types.{CloudAgent, Transaction}

@Component
class PollingService(agent: CloudAgent) {

  def start(): Unit = {

  }

  def stop(): Unit = {

  }
}
