package tom.ff.etl

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, ComponentScan}
import tom.ff.etl.domain.ETLTypes.QueueClient
import tom.ff.etl.io.RabbitMQClient


@SpringBootApplication
@ComponentScan(
  basePackages = Array(
    "tom.ff.etl"
  )
)
class ETLApp() {

  @Bean(initMethod = "consume")
  def queueClient(
                   @Value("${queue.name}") queueName: String,
                   @Value("${rmq.broker}") rmqBroker: String,
                   @Value("${elastic.host}") elasticHost: String
                 ): QueueClient = {

    new RabbitMQClient(queueName, rmqBroker, elasticHost)
  }
}

object ETLApplication {
  def main(args: Array[String]) {
    SpringApplication.run(classOf[ETLApp])
  }
}
