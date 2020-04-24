package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.Types._

import scala.collection.mutable.ArrayBuffer

object Workflows {

  val fetch: Fetch = (connector: Connector) => {

    def parseRawTransaction(obj: String): RawTransaction = {
      val toks = obj.split(",")

      val rawDebitCredit = toks(6).charAt(0)
      val debitCredit = rawDebitCredit match {
        case 'd' => Debit()
        case 'c' => Credit()
      }

      RawTransaction(
        Originator(
          toks(0),
          AccountNumber(toks(1).toLong)
        ),
        Beneficiary(
          toks(2),
          AccountNumber(toks(3).toInt)
        ),
        Money(toks(4).toDouble, toks(5)),
        debitCredit
      )
    }

    val objects: Seq[Any] = connector.getObjects
    println(s"Fetch received ${objects.size} raw objects")

    val results = new ArrayBuffer[Result[FetchError, RawTransaction]]

    objects.map(o => {
      try {
        val rawTransaction = new String(o.asInstanceOf[Array[Byte]], StandardCharsets.UTF_8)
        val txn = parseRawTransaction(rawTransaction)
        results += (Result(Right(txn)))
      }
      catch {
        case e: RuntimeException => {
          results += Result(Left(new FetchError(e.getLocalizedMessage, o.getClass.toString)))
        }
      }
    })

    println(s"Fetch produced ${results.size} results")
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
