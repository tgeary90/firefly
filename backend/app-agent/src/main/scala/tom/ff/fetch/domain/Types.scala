package tom.ff.fetch.domain


object Types {

  /////// WorkFlows ///////////

  class FetchError(msg: String, fails: List[FailedTransaction]) extends RuntimeException {
    def getFailedTransactions: String = fails.mkString(", ")
  }

  class JobError(msg: String) extends RuntimeException

  type Result[A, B] = Either[A, B]
  type FailedTransaction = String
  type Ack = String

  type fetch = CloudAgent => Result[FetchError, List[Transaction]]
  type createJob = List[Transaction] => Result[JobError, Job[Transaction]]
  type enqueue = Job[Transaction] => Result[JobError, Ack]

  /////// value objects ///////


  trait CloudAgent {
    def fetchTransactions(bucket: String): List[Any]
  }

  trait CloudService {
    def start(cloudAgent: CloudAgent): Unit
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

  //////// Entities ///////////

  case class Bucket(url: String)

  case class Job[T](id: Int)

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
