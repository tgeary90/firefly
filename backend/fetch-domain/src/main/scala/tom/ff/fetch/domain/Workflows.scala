package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.Types._

import scala.collection.mutable.ArrayBuffer

object Workflows {

  val fetch: Fetch = (connector: Connector) => {

    def parseTransactions(file: Any): Seq[String] = {
      val content = new String(file.asInstanceOf[Array[Byte]], StandardCharsets.UTF_8)
      val lines: Seq[String] = content.split("\n")
      lines
    }

    def parseRawTransaction(obj: String): RawTransaction = {
      val toks = obj.split(",")

      val rawDebitCredit = toks(6).charAt(0)
      val debitCredit = rawDebitCredit match {
        case 'd' => Debit()
        case 'c' => Credit()
      }

      RawTransaction(
        Originator(
          toks(1),
          AccountNumber(toks(0).toLong)
        ),
        Beneficiary(
          toks(3),
          AccountNumber(toks(2).toInt)
        ),
        Money(toks(4).toDouble, toks(5)),
        debitCredit
      )
    }

    val files: Seq[Any] = connector.getObjects
    println(s"Fetch received ${files.size} raw objects")

    val results = new ArrayBuffer[Result[FetchError, RawTransaction]]

    files.map(file => {
      try {
        val lines: Seq[String] = parseTransactions(file)

        lines.foreach {
           line => {
             val txn = parseRawTransaction(line)
             results += (Result(Right(txn)))
           }
        }
      }
      catch {
        case e: RuntimeException => {
          results += Result(Left(new FetchError(e.getLocalizedMessage, file.getClass.toString)))
        }
      }
    })

    val oks = results.filter(r => r.result.isRight).size
    val fails = results.filter(r => r.result.isLeft).size
    println(s"Fetch produced ${oks} transactions and ${fails} failures")
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
