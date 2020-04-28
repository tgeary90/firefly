package tom.ff.etl.domain

import scala.collection.mutable.{Map => MMap}


object ETLTypes {

  /////// WorkFlows ///////////

  class JobError(msg: String, e: Throwable) extends RuntimeException
  class LoadError(msg: String) extends RuntimeException

  type Dequeue = Array[Byte] => Result[Seq[RawTransaction]]
  type Validate = Seq[RawTransaction] => Result[Seq[ValidatedTransaction]]
  type Load = Seq[ValidatedTransaction] => Result[Seq[LoadResponse]]

  /////// Value Objects ///////

  case class LoadResponse(documentId: String)

  trait Result[+A] {
    def map[B](f: A => B): Result[B]
    def flatMap[B](f: A => Result[B]): Result[B]
    def get: Any
  }

  // pass in by-name so that the result in
  // constructed safely within the function
  object Result {
    def apply[A](value: => A): Result[A] = {
      try {
        Success(value)
      }
      catch {
        case e: RuntimeException => Fail(e)
      }
    }
  }

  case class Success[A](value: A) extends Result[A] {
    override def map[B](f: A => B): Result[B] = Result[B](f(value))
    override def flatMap[B](f: A => Result[B]): Result[B] = f(value)
    override def get: A = value
  }

  case class Fail(e: RuntimeException) extends Result[Nothing] {
    override def map[B](f: Nothing => B): Result[B] = this
    override def flatMap[B](f: Nothing => Result[B]): Result[B] = this
    override def get: String = e.getLocalizedMessage
  }

  trait LoadClient {
    def load(txns: Seq[ValidatedTransaction]): Seq[LoadResponse]
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

  case class Job[T](size: Int, payload: Seq[T])

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
