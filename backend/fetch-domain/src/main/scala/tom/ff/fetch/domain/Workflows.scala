package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.Types._

import scala.collection.mutable.ArrayBuffer

object Workflows {

  val fetch: Fetch = (connector: Connector) => {
    val objects: Seq[Any] = connector.getObjects
    println(s"Fetch received ${objects.size} raw objects")

    val results = new ArrayBuffer[Result[FetchError, RawTransaction]]

    objects
      .collect {
        case bytes: Array[Byte] => {
          try {
            // TODO this is not DataInputStream deserialization. Need custom GCP deserializer. ERROR on run
            val txn = bytes.deserialize[RawTransaction]
            results += Result(Right(txn))

          }
          catch {
            case e: RuntimeException => {
              results += Result(Left(new FetchError(e.getLocalizedMessage, List(new String(bytes, StandardCharsets.UTF_8)))))
            }
          }
        }
      }

    println(s"Fetch produced ${results.size} transactions")
    results.toSeq
  }

  val createJob: CreateJob = (txns: Seq[RawTransaction]) => {
    val job = Job[RawTransaction](txns.size, txns)
    println(s"Created job with ${txns.size} txns")
    Result(Right(job))
  }

  val enqueue: Enqueue = (c: QueueClient, job: Job[RawTransaction]) => {
    try {
      c.produce(job.serialize)
      println(s"Enqueued job with ${job.size} objects")
      Result(Right("Success"))
    }
    catch {
      case e: Throwable => Result(Left(new JobError(e.getLocalizedMessage)))
    }
  }
}
