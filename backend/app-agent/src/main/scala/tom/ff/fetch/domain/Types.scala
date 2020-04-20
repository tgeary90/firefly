package tom.ff.fetch.domain


object Types {

  /////// WorkFlows ///////////

  class FetchError(msg: String, fails: List[FailedTransaction]) extends RuntimeException {
    def getFailedTransactions: String = fails.mkString(", ")
  }

  class JobError(msg: String) extends RuntimeException

  type FailedTransaction = String
  type Ack = String

  type Fetch = Connector => Seq[Result[FetchError,Transaction]]
  type CreateJob = List[Transaction] => Result[JobError, List[Job[Transaction]]]
  type Enqueue = (QueueClient, List[Job[Transaction]]) => Result[JobError, Ack]

  /////// Value Objects ///////

  case class Result[A, B](result: Either[A, B])

  trait Connector {
    def getObjects(): Seq[Any]
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

  case class Job[T](transaction: Array[Byte])

  //////// Entities ///////////

  class Bucket(val id: Int, val url: String) {
    def canEqual(that: Any): Boolean = that.isInstanceOf[Bucket]

    override def equals(that: Any): Boolean = {
      that match {
        case b: Bucket => {
          (this eq b) || (b.canEqual(this)) && (hashCode == b.hashCode()) && (id == b.id) && (url == b.url)
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
