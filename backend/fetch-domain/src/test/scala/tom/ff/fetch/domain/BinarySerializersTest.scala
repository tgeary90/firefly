package tom.ff.fetch.domain

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import tom.ff.fetch.domain.BinarySerializers._
import tom.ff.fetch.domain.FetchTypes._

@RunWith(classOf[JUnitRunner])
class BinarySerializersTest extends FlatSpec {

  "Job" should "Serialize and Deserialize" in {
    val job = Job(2,
      Seq(
        RawTransaction(
          Originator("mickey", AccountNumber(12345678)),
          Beneficiary("mallory", AccountNumber(87654321)),
          Money(10.0, "stirling"),
          Debit()
        ),
        RawTransaction(
          Originator("han sole", AccountNumber(12345633)),
          Beneficiary("luke skywalker", AccountNumber(87654344)),
          Money(20.0, "creds"),
          Credit()
        )
      ),
      JobMetadata("ETL", "gcp")
    )

    val jobBytes: Array[Byte]                 = job.serialize
    val deserializedJob: Job[RawTransaction]  = jobBytes.deserialize[Job[RawTransaction]]

    assert(job == deserializedJob)
  }

  "Practice: DataStreams" should "serialize and deserialize correctly" in {

    case class Transaction(id: Int, stuff: String)

    val bos                = new ByteArrayOutputStream()
    val dos                = new DataOutputStream(bos)
    val txnToSerialize     = Transaction(1, "hello, world")
    dos.writeInt(txnToSerialize.id)
    dos.writeUTF(txnToSerialize.stuff)

    val bis                = new ByteArrayInputStream(bos.toByteArray)
    val dis                = new DataInputStream(bis)
    val deserializedTxn    = Transaction(dis.readInt(), dis.readUTF())

    println(deserializedTxn)
    assert(txnToSerialize == deserializedTxn)
  }
}
