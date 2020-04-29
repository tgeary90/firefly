package tom.ff.fetch.domain
import scala.collection.mutable.{Map => MMap}


object FetchTypes {

  /////// WorkFlows ///////////

  class FetchError(msg: String, failedTransaction: FailedTransaction) extends RuntimeException {
    def getFailedTransaction: String = failedTransaction
  }

  class JobError(msg: String) extends RuntimeException

  type FailedTransaction = String
  type Ack = String
  type FileTable = MMap[Provider, Set[FileName]]

  // note RawTransactions. ETL workflow to validate.
  type Fetch = (Connector, FileTable) => Option[Seq[Result[FetchError, RawTransaction]]]
  type CreateJob = (Connector, Seq[RawTransaction]) => Result[JobError, Job[RawTransaction]]
  type Enqueue = (QueueClient, Job[RawTransaction]) => Result[JobError, Ack]

  /////// Value Objects ///////

  type FileName = String
  type Provider = String

  case class Result[A, B](result: Either[A, B])

  trait Connector {
    def getObjects(): Seq[(String, Any)]
    def getProviderName(): String
  }

  trait QueueClient {
    def produce(bytes: Array[Byte]): Unit
    def consume(): Array[Byte]
  }

  case class AccountNumber(value: Long)

  abstract sealed trait DebitCredit
  case class Debit() extends DebitCredit
  case class Credit() extends DebitCredit

  case class Money(quantity: Double, currency: String)

  abstract sealed class Transaction
  case class RawTransaction(
                             orig: Originator,
                             bene: Beneficiary,
                             amount: Money,
                             debitCredit: DebitCredit
                           ) extends Transaction

  case class ValidatedTransaction(
                                   orig: Originator,
                                   bene: Beneficiary,
                                   amount: Money,
                                   debitCredit: DebitCredit
                                 ) extends Transaction

  case class Job[T](size: Int, payload: Seq[T], metadata: JobMetadata)
  case class JobMetadata(jobType: String, provider: String)

  //////// Entities ///////////

  class Bucket(val id: Int, val url: String, numObjects: Long, lastETLDate: java.sql.Date) {
    def canEqual(that: Any): Boolean = that.isInstanceOf[Bucket]

    override def equals(that: Any): Boolean = {
      that match {
        case b: Bucket => {
          (this eq b) || (b.canEqual(this)) && (hashCode == b.hashCode()) && (id == b.id)
        }
        case _ => false
      }
    }

    override def hashCode(): Int = 31 * ( id.## ) + id.##
  }

  abstract class Originator {
    def accNo: AccountNumber
    def name: String
  }

  object Originator {
    def apply(name: String, accNo: AccountNumber): Originator =
      if (accNo.value.toString.size == 8) new OriginatorImpl(name, accNo)
      else throw new IllegalArgumentException("Invalid Account Number")

    private class OriginatorImpl(str: String, number: AccountNumber) extends Originator {
      override def equals(that: Any): Boolean = that match {
        case that: Originator => {
          this.accNo == that.accNo
        }
        case _ => false
      }

      override def hashCode(): Int = {
        val prime = 31
        var result = 1
        result = prime * result + accNo.value.toInt
        result = prime * result + (if (accNo == null) 0 else name.hashCode)
        result
      }

      override def accNo: AccountNumber = number
      override def name: String = str
    }
  }

  abstract class Beneficiary {
    def accNo: AccountNumber
    def name: String
  }

  object Beneficiary {
    def apply(name: String, accNo: AccountNumber): Beneficiary =
      if (accNo.value.toString.size == 8) new BeneficiaryImpl(name, accNo)
      else throw new IllegalArgumentException("Invalid Account Number")

    private class BeneficiaryImpl(str: String, number: AccountNumber) extends Beneficiary {
      override def equals(that: Any): Boolean = that match {
        case that: Beneficiary => {
          this.accNo == that.accNo
        }
        case _ => false
      }

      override def hashCode(): Int = {
        val prime = 31
        var result = 1
        result = prime * result + accNo.value.toInt
        result = prime * result + (if (accNo == null) 0 else name.hashCode)
        result
      }

      override def accNo: AccountNumber = number
      override def name: String = str
    }
  }
}
