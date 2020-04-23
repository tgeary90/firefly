package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import org.slf4j.{Logger, LoggerFactory}
import tom.ff.fetch.domain.Types._
import tom.ff.fetch.io.BinarySerializers._

import scala.collection.mutable.ArrayBuffer

object Workflows {

  val log: Logger = LoggerFactory.getLogger("Workflows")

  val fetch: Fetch = (connector: Connector) => {
    val objects: Seq[Any] = connector.getObjects
    log.info(s"Fetch received ${objects.size} raw objects")

    val results = new ArrayBuffer[Result[FetchError, Transaction]]

    objects
      .collect {
        case bytes: Array[Byte] => {
          try {
            val txn = DeserializeOps.fromBinary[Transaction](bytes)
            results += Result(Right(txn))

          }
          catch {
            case e: RuntimeException => {
              results += Result(Left(new FetchError(e.getLocalizedMessage, List(new String(bytes, StandardCharsets.UTF_8)))))
            }
          }
        }
      }

    log.info(s"Fetch produced ${results.size} transactions")
    results.toSeq
  }

  val createJob: CreateJob = (txns: Seq[Transaction]) => {
    val job = Job[Transaction](txns.size, txns)
    log.info(s"Created job with ${txns.size} txns")
    Result(Right(job))
  }

  val enqueue: Enqueue = (c: QueueClient, job: Job[Transaction]) => {
    try {
      c.produce(job.toBinary)
      log.info(s"Enqueued job with ${job.size} objects")
      Result(Right("Success"))
    }
    catch {
      case e: Throwable => Result(Left(new JobError(e.getLocalizedMessage)))
    }
  }
}
