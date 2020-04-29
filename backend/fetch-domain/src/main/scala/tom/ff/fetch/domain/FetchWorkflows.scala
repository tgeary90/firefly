package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.FetchTypes._

import scala.collection.mutable.ArrayBuffer

object FetchWorkflows {

  val fetch: Fetch = (connector: Connector, fileTable: FileTable) => {
    def addToFileTable(fileTable: FileTable, provider: String, file: (String, Any)): Unit = {
      val fileName = file match { case (name, _) => name }

      if (fileTable.contains(provider)) {
        val filesForProvider = fileTable(provider)
        fileTable += (provider -> (filesForProvider + fileName))
      }
      else {
        fileTable += (provider -> Set(fileName))
      }
    }

    def isInFileTable(fileTable: FileTable, provider: Provider, file: (String, Any)): Boolean = {
      val fileName = file match { case (name, _) => name }

      if (fileTable.contains(provider)) {
        fileTable(provider).contains(fileName)
      }
      else false
    }

    def parseTransactions(file: Any): Seq[String] = {
      val bytes = file.asInstanceOf[Array[Byte]]
      val content = new String(bytes, StandardCharsets.UTF_8)
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

    val fileList: Seq[(String, Any)] = connector.getObjects

    val results = new ArrayBuffer[Result[FetchError, RawTransaction]]

    fileList.foreach(
      file => {
        try {
          if ( ! isInFileTable(fileTable, connector.getProviderName(), file)) {
            addToFileTable(fileTable, connector.getProviderName(), file)
            val lines: Seq[String] = parseTransactions(file match { case (_, bytes) => bytes })

            lines.foreach {
               line => {
                 val txn = parseRawTransaction(line)
                 results += (Result(Right(txn)))
               }
            }
          }
        }
        catch {
          case e: RuntimeException => {
            results += Result(Left(new FetchError(e.getLocalizedMessage, file.getClass.toString)))
          }
        }
      }
    )

    val oks = results.filter(r => r.result.isRight).size
    val fails = results.filter(r => r.result.isLeft).size

    if (results.size == 0) None else {
      println(s"Fetch produced ${oks} transactions and ${fails} failures")
      Some(results.toSeq)
    }
  }

  val createJob: CreateJob = (connector: Connector, txns: Seq[RawTransaction]) => {
    val job = Job[RawTransaction](txns.size, txns, JobMetadata("ETL", connector.getProviderName()))
    println(s"Created ETL job for ${connector.getProviderName()} with ${txns.size} txns")
    Result(Right(job))
  }

  val enqueue: Enqueue = (c: QueueClient, job: Job[RawTransaction]) => {
    try {
      if (job.payload.size > 0){
        c.produce(job.serialize)
        println(s"Enqueued job with ${job.size} objects")
        Result(Right("Success"))
      }
      else Result(Right("No op"))
    }
    catch {
      case e: Throwable => Result(Left(new JobError(e.getLocalizedMessage)))
    }
  }
}
