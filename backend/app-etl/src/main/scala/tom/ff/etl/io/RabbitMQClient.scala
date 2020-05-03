package tom.ff.etl.io

import com.rabbitmq.client._
import org.slf4j.{Logger, LoggerFactory}
import tom.ff.etl.domain.ETLTypes._
import tom.ff.etl.domain.ETLWorkflows

class RabbitMQClient(
                      queueName: String,
                      rmqBroker: String
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
      case e: RuntimeException => throw new JobError("could not send message", e)
    }
  }

  def consume(): Unit = {
    try {
      log.info(s"Awaiting messages...")

      val deliverCallback = new DeliverCallback {
        override def handle(consumerTag: String, message: Delivery): Unit = {
          log.info(s"Received message of ${message.getBody.length} bytes")

          val responseList = for {
            (txns, ctx)           <- ETLWorkflows.dequeue(message.getBody)
            validatedTransactions <- ETLWorkflows.validate(txns)
            responses             <- ElasticClientFactory.getLoaderFlow()(ctx, validatedTransactions)
          } yield responses

          // TODO do something with the responses - log ?
        }
      }

      val cancel = new CancelCallback {
        override def handle(consumerTag: String): Unit = {}
      }

      channel.basicConsume(queueName, true, deliverCallback, cancel)
    }
    catch {
      case e: RuntimeException => throw new JobError("Could not consume message", e)
    }
  }
}
