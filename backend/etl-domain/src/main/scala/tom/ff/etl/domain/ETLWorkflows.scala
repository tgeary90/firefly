package tom.ff.etl.domain

import tom.ff.etl.domain.ETLTypes._
import tom.ff.etl.domain.BinarySerializers._

object ETLWorkflows {

  val dequeue: Dequeue = (bytes: Array[Byte]) => {
    val dequeueJob = (bytes: Array[Byte]) => {
      val job = bytes.deserialize[Job[RawTransaction]]
      (job.payload, job.metadata)
    }
    val result = Result(dequeueJob(bytes))
    result
  }

  val validate: Validate = (rawTxns: Seq[RawTransaction]) => {
    type TransactionValidation = RawTransaction => Boolean

    // predicate factories
    val amountIsNotNegative: TransactionValidation        = txn => txn.amount.quantity > 0d
    val amountIsBelow: (Double) => TransactionValidation  = n => txn => txn.amount.quantity <= n

    def validateTransactions(ts: Seq[RawTransaction], f: TransactionValidation): Seq[RawTransaction] = {
      ts.filter(f)
    }

    val txnsPass1 = validateTransactions(rawTxns, amountIsBelow(1e6))
    val txnsPass2 = validateTransactions(txnsPass1, amountIsNotNegative)

    Result(
      txnsPass2.map(
        t => ValidatedTransaction(
          t.orig,
          t.bene,
          t.amount,
          t.debitCredit
        )
      )
    )
  }
}
