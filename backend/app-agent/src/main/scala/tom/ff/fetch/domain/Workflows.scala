package tom.ff.fetch.domain

import tom.ff.fetch.domain.Types._
import tom.ff.fetch.io.BinarySerializers._

object Workflows {

  val fetch: Fetch = (connector: Connector, bucket: Bucket) => {
    val objects: Seq[Any] = connector.getObjects(bucket.url)

    val txns: List[Transaction] = objects
      .filter(o => o match {
        case t: RawTransaction  => true
        case _                  => false
      })
      .map(o => {
        val txn = o.asInstanceOf[RawTransaction]
        RawTransaction(txn.orig, txn.bene, txn.amount, txn.debitCredit)
        }
      ).toList

    Result(Right(txns))
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
