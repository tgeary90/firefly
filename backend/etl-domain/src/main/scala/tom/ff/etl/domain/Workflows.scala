package tom.ff.etl.domain

import tom.ff.etl.domain.ETLTypes._
import tom.ff.etl.domain.BinarySerializers._

object Workflows {

/*
  type Dequeue = (Array[Byte]) => Result[Seq[RawTransaction]]
  type Validate = (Seq[RawTransaction]) => Result[Seq[ValidatedTransaction]]
  type Load = Seq[ValidatedTransaction] => Result[LoadResponse]
 */

  val dequeue: Dequeue = (bytes: Array[Byte]) => {
    Result(bytes.deserialize[Job[RawTransaction]].payload)
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

  val load: Load = (validatedTxns: Seq[ValidatedTransaction]) => {
    null
  }
}
