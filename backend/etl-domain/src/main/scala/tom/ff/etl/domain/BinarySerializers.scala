package tom.ff.etl.domain

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream}

import tom.ff.etl.domain.ETLTypes._

object BinarySerializers {

    // 1. template
    // 2. instance
    // 3. interface (object / enhancement (syntax)

    // Template

    trait Serialize[T] {
        def toBinaryStream(value: T, dos: DataOutputStream): Unit
        def fromBinaryStream(dis: DataInputStream): T
    }

    // instances

    implicit object AccountNumberSerializer extends Serialize[AccountNumber] {
        override def toBinaryStream(value: AccountNumber, dos: DataOutputStream): Unit = {
          dos.writeLong(value.value)
        }
        override def fromBinaryStream(dis: DataInputStream): AccountNumber = {
          AccountNumber(dis.readLong())
        }
    }

    implicit object DebitCreditSerializer extends Serialize[DebitCredit] {
      override def toBinaryStream(value: DebitCredit, dos: DataOutputStream): Unit = {
          value match {
            case Debit() => dos.writeByte('d')
            case Credit() => dos.writeByte('c')
          }
      }

      override def fromBinaryStream(dis: DataInputStream): DebitCredit = {
        dis.readByte() match {
          case 'd' => Debit()
          case 'c' => Credit()
        }
      }
    }

    implicit object MoneySerializer extends Serialize[Money] {
      override def toBinaryStream(value: Money, dos: DataOutputStream): Unit = {
        dos.writeDouble(value.quantity)
        dos.writeUTF(value.currency)
      }

      override def fromBinaryStream(dis: DataInputStream): Money = {
        Money(
          dis.readDouble(),
          dis.readUTF()
        )
      }
    }

    implicit object BeneficiarySerializer extends Serialize[Beneficiary] {
      override def toBinaryStream(value: Beneficiary, dos: DataOutputStream): Unit = {
        dos.writeUTF(value.name)
        value.accNo.toBinaryStream(dos)
      }

      override def fromBinaryStream(dis: DataInputStream): Beneficiary = {
        Beneficiary(dis.readUTF(), dis.fromBinaryStream[AccountNumber])
      }
    }

    implicit object OriginatorSerializer extends Serialize[Originator] {
      override def toBinaryStream(value: Originator, dos: DataOutputStream): Unit = {
        dos.writeUTF(value.name)
        value.accNo.toBinaryStream(dos)
      }

      override def fromBinaryStream(dis: DataInputStream): Originator = {
        Originator(dis.readUTF(), dis.fromBinaryStream[AccountNumber])
      }
    }

    implicit object TransactionSerializer extends Serialize[RawTransaction] {
      override def toBinaryStream(value: RawTransaction, dos: DataOutputStream): Unit = {
        value.orig.toBinaryStream(dos)
        value.bene.toBinaryStream(dos)
        value.amount.toBinaryStream(dos)
        value.debitCredit.toBinaryStream(dos)
      }

      override def fromBinaryStream(dis: DataInputStream): RawTransaction = {
        RawTransaction(
          dis.fromBinaryStream[Originator],
          dis.fromBinaryStream[Beneficiary],
          dis.fromBinaryStream[Money],
          dis.fromBinaryStream[DebitCredit]
        )
      }
    }

  implicit object JobMetadataSerializer extends Serialize[JobMetadata] {
    override def toBinaryStream(value: JobMetadata, dos: DataOutputStream): Unit = {
      dos.writeUTF(value.jobType)
      dos.writeUTF(value.provider)
    }

    override def fromBinaryStream(dis: DataInputStream): JobMetadata = {
      JobMetadata(dis.readUTF(), dis.readUTF())
    }
  }

  implicit object JobSerializer extends Serialize[Job[RawTransaction]] {
    override def toBinaryStream(value: Job[RawTransaction], dos: DataOutputStream): Unit = {
      dos.writeInt(value.size)
      value.payload.foreach(txn => txn.toBinaryStream(dos))
      value.metadata.toBinaryStream(dos)
    }

    override def fromBinaryStream(dis: DataInputStream): Job[RawTransaction] = {
      val jobSize = dis.readInt
      Job(
        jobSize,
        for {
          _ <- 1 to jobSize
        } yield dis.fromBinaryStream[RawTransaction],
        dis.fromBinaryStream[JobMetadata]
      )
    }
  }

  // 3. interface (enrichment)

  implicit class StreamToOps[T](value: T) {
    def toBinaryStream(dos: DataOutputStream)(implicit serializer: Serialize[T]): Unit = {
      serializer.toBinaryStream(value, dos)
    }
  }

  implicit class StreamFromOps[T](dis: DataInputStream) {
    def fromBinaryStream[T](implicit serializer: Serialize[T]): T = {
      serializer.fromBinaryStream(dis)
    }
  }

  implicit class SerializeOps[T](value: T) {
      def serialize(implicit serializer: Serialize[T]): Array[Byte] = {
        val (dos, bos) = getOutputDataStreams()
        serializer.toBinaryStream(value, dos)
        bos.toByteArray
      }
  }

  implicit class DeserializeOps(value: Array[Byte]) {
      def deserialize[T](implicit serializer: Serialize[T]): T = {
        val dis = getInputDataStream(value)
        serializer.fromBinaryStream(dis)
      }
  }


  ///////////// Helper Functions //////////////////////

  private def getInputDataStream(bytes: Array[Byte]): DataInputStream = {
      val bis = new ByteArrayInputStream(bytes)
      val dis = new DataInputStream(bis)
      dis
  }

  private def getOutputDataStreams(): (DataOutputStream, ByteArrayOutputStream) = {
    val bos = new ByteArrayOutputStream()
    val dos = new DataOutputStream(bos)
    (dos, bos)
  }
}
