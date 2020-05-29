package tom.ff.fetch.domain

import java.nio.charset.StandardCharsets

import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.FetchTypes._

import scala.collection.mutable.ArrayBuffer

object FetchWorkflows {

  val fetchObjects: Fetch = (connector: Connector, fileTable: FileTable, bucketMetadata: BucketMetadata) => {

    //////// helper functions to fetch workflow //////////////

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

    def addTxnsToList(rawTransactions: ArrayBuffer[RawTransaction], lines: Seq[String]) = {
      lines.foreach {
        line => {
          val txn = parseRawTransaction(line)
          rawTransactions += txn
        }
      }
    }

    //////////////////////////////////////////////////////////

    val buckets = connector.listBuckets()

    for {
      bucket  <- buckets
    } yield {
      bucketMetadata.updateBucketETLMetadata(
        bucket, connector.getBucketCount(bucket), connector.getProviderName()
      )
    }

    val fileList: Seq[(String, Any)] = for {
      bucket  <- buckets
      files   <- connector.getBucketContents(bucket)
    } yield files

    Result {
      val rawTransactions = new ArrayBuffer[RawTransaction]
      val failures        = new ArrayBuffer[String]

      fileList.foreach(
        file => {
          try {
            if (!isInFileTable(fileTable, connector.getProviderName(), file)) {
              addToFileTable(fileTable, connector.getProviderName(), file)
              val lines: Seq[String] = parseTransactions(file match { case (_, bytes) => bytes })
              addTxnsToList(rawTransactions, lines)
            }
          }
          catch {
            case e: RuntimeException => {
              // dont want to bust the whole of the fetch process
              // so just print out the duffer.
              println(s"duff transaction ${file._1}")
              failures += file._1
            }
          }
        }
      )
      println(s"Fetch produced ${rawTransactions.size} transactions and ${failures.size} failures")
      rawTransactions.toSeq
    }
  }

  val createJob: CreateJob = (connector: Connector, txns: Seq[RawTransaction]) => {
    Result {
      val job = Job[RawTransaction](txns.size, txns, JobMetadata("ETL", connector.getProviderName()))
      println(s"Created ETL job for ${connector.getProviderName()} with ${txns.size} txns")
      job
    }
  }

  val enqueue: Enqueue = (c: QueueClient, job: Job[RawTransaction]) => {
    Result {
        if (job.payload.size > 0){
          c.produce(job.serialize)
          println(s"Enqueued job with ${job.size} objects")
        }
        "no op"
      }
    }
}
