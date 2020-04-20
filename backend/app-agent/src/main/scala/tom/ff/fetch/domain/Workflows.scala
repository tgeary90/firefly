package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import org.slf4j.{Logger, LoggerFactory}
import tom.ff.fetch.domain.Types._
import tom.ff.fetch.io.BinarySerializers._

import scala.collection.mutable.ArrayBuffer

object Workflows {

  val log: Logger         = LoggerFactory.getLogger("FetchWF")

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

  val createJob: CreateJob = (txns: List[Transaction]) => {
    val jobs: List[Job[Transaction]] = txns.map {
      t => Job.apply[Transaction](t.toBinary)
    }

    Result(Right(jobs))
  }

  val enqueue: Enqueue = (c: QueueClient, jobs: List[Job[Transaction]]) => {
    try {
      jobs.map {
        job => c.produce(job.transaction)
      }
      Result(Right("Success"))
    }
    catch {
      case e: Throwable => Result(Left(new JobError(e.getLocalizedMessage)))
    }
  }
}
