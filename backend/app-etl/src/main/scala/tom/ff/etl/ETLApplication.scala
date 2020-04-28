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
class AppConfig() {

  @Bean(initMethod = "consume")
  def queueClient(
                   @Value("${queue.name}") queueName: String,
                   @Value("${rmq.broker}") rmqBroker: String
                 ): QueueClient = {

    new RabbitMQClient(queueName, rmqBroker)
  }
}

object ETLApplication extends App {
    SpringApplication.run(classOf[AppConfig])
}
