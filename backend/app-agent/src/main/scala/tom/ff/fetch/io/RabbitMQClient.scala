package tom.ff.fetch.io

import com.rabbitmq.client.{CancelCallback, Channel, ConnectionFactory, DeliverCallback, Delivery}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import tom.ff.fetch.domain.FetchTypes.{JobError, QueueClient}

@Component
class RabbitMQClient(
                      @Value("${queue.name}") queueName: String,
                      @Value("${rmq.broker}") rmqBroker: String
                    ) extends QueueClient {

  val factory: ConnectionFactory = new ConnectionFactory
  factory.setHost(rmqBroker)
  val conn                       = factory.newConnection()
  val channel: Channel           = conn.createChannel()
  val log: Logger                = LoggerFactory.getLogger("RMQClient")
  channel.queueDeclare(queueName, false, false, false, null)

  def produce(bytes: Array[Byte]): Unit = {
    try {
      log.info(s"Enqueueing ${bytes.length}b")
      channel.basicPublish("", queueName, null, bytes)
    } catch {
      case e: RuntimeException => throw new JobError("could not send message")
    }
  }

  def consume(): Array[Byte] = {
    var received: Array[Byte] = null

    try {
      channel.queueDeclare(queueName, false, false, false, null)
      log.info(s"Awaiting messages...")

      val deliverCallback = new DeliverCallback {
        override def handle(consumerTag: String, message: Delivery): Unit = {
          log.info(s"Received message of ${message.getBody.length} bytes")
          received = message.getBody
        }
      }

      val cancel = new CancelCallback {
        override def handle(consumerTag: String): Unit = {}
      }

      channel.basicConsume(queueName, true, deliverCallback, cancel)
      received
    }
    catch {
      case e: RuntimeException => throw new JobError("Could not consume message")
    }
  }
}
