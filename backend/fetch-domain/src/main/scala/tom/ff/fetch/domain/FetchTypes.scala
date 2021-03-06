package tom.ff.fetch.domain

import java.util.Date

import scala.collection.mutable.{Map => MMap}

object FetchTypes {

  class FetchError(msg: String, failedTransaction: FailedTransaction) extends RuntimeException {
    def getFailedTransaction: String = failedTransaction
  }

  class JobError(msg: String) extends RuntimeException

  type FailedTransaction  = String
  type FileTable          = MMap[Provider, Set[FileName]]

  /////// Service /////////////

  trait BucketMetadata {
    def getBucketsFor(provider: String): Seq[Bucket]
    def updateBucketETLMetadata(bucketName: String, count: Int, provider: Provider): Unit
  }

  /////// WorkFlows ///////////

  type Fetch =      (Connector, FileTable, BucketMetadata)  => Result[Seq[RawTransaction]]
  type CreateJob =  (Connector, Seq[RawTransaction])        => Result[Job[RawTransaction]]
  type Enqueue =    (QueueClient, Job[RawTransaction])      => Result[String]

  /////// Value Objects ///////

  type FileName = String
  type Provider = String

  trait Result[+A] {
    def map[B](f: A => B): Result[B]
    def flatMap[B](f: A => Result[B]): Result[B]
    def withFilter(predicate: A => Boolean): Result[A]
  }

  object Result {
    def apply[A](value: => A): Result[A] = {
      try {
        SuccessResult(value)
      }
      catch {
        case e: Throwable => FailureResult(new JobError(e.getLocalizedMessage))
      }
    }
  }

  case class SuccessResult[A](value: A) extends Result[A] {
    override def map[B](f: A => B): Result[B] = Result[B](f(value))
    override def flatMap[B](f: A => Result[B]): Result[B] = f(value)
    override def withFilter(predicate: A => Boolean): Result[A] = if (predicate(value)) this else null
  }

  case class FailureResult(e: Throwable) extends Result[Nothing] {
    override def map[B](f: Nothing => B): Result[B] = this
    override def flatMap[B](f: Nothing => Result[B]): Result[B] = this
    override def withFilter(predicate: Nothing => Boolean): Result[Nothing] = this
  }

  trait Connector {
    def getBucketContents(bucketName: String): Seq[(String, Any)]
    def getProviderName(): String
    def getBucketCount(bucketName: String): Int
    def listBuckets(): Seq[String]
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

  case class Bucket(
                     name: String,
                     numObjects: Long,
                     lastETLDate: Date,
                     provider: String
                   )

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
