//package tom.ff.fetch.io
//
//import java.io.{ByteArrayInputStream, DataInputStream}
//import java.nio.charset.StandardCharsets
//
//import org.tom.firefly.model.common.DomainTypes._
//
//object BinarySerializers {
//
//    // 1. template
//    // 2. instance
//    // 3. interface (object / enhancement (syntax)
//
//    // Template
//
//    trait Serialize[T] {
//        def toBinary(value: T): Array[Byte]
//        def fromBinary(bytes: Array[Byte]): T
//    }
//
//    // instances
//
//    implicit object AccountNumberSerializer extends Serialize[AccountNumber] {
//        override def toBinary(value: AccountNumber): Array[Byte] = {
//            value.toString.getBytes(StandardCharsets.UTF_8)
//        }
//        override def fromBinary(bytes: Array[Byte]): AccountNumber = {
//            new AccountNumber(new String(bytes, StandardCharsets.UTF_8).toLong)
//        }
//    }
//
//    implicit object DebitCreditSerializer extends Serialize[DebitCredit] {
//        override def toBinary(value: DebitCredit): Array[Byte] = {
//            value match {
//                case _: Debit => Array[Byte]('d')
//                case _: Credit => Array[Byte]('c')
//            }
//        }
//
//        override def fromBinary(bytes: Array[Byte]): DebitCredit = {
//            bytes(0) match {
//                case 'd' => Debit()
//                case 'c' => Credit()
//            }
//        }
//    }
//
//    implicit object MoneySerializer extends Serialize[Money] {
//        override def toBinary(value: Money): Array[Byte] = {
//            val quantityBytes = value.quantity.toString.getBytes(StandardCharsets.UTF_8)
//            val currencyBytes = value.currency.getBytes(StandardCharsets.UTF_8)
//            (quantityBytes ++ currencyBytes).toArray[Byte]
//        }
//
//        override def fromBinary(bytes: Array[Byte]): Money = {
//            val dis: DataInputStream = getDataStreamFor(bytes)
//            Money(
//                dis.readDouble(),
//                dis.readUTF()
//            )
//        }
//    }
//
//    implicit object BeneficiarySerializer extends Serialize[Beneficiary] {
//        override def toBinary(value: Beneficiary): Array[Byte] = {
//            val accNoBytes = value.accNo.toBinary.toList
//            val nameBytes = value.name.getBytes(StandardCharsets.UTF_8)
//            (accNoBytes ++ nameBytes).toArray[Byte]
//        }
//
//        override def fromBinary(bytes: Array[Byte]): Beneficiary = {
//            Beneficiary(
//                DeserializeOps.fromBinary(bytes),
//                DeserializeOps.fromBinary(bytes)
//            )
//        }
//    }
//
//    implicit object OriginatorSerializer extends Serialize[Originator] {
//        override def toBinary(value: Originator): Array[Byte] = {
//            val accNoBytes = value.accNo.toBinary.toList
//            val nameBytes = value.name.getBytes(StandardCharsets.UTF_8)
//            (accNoBytes ++ nameBytes).toArray[Byte]
//        }
//
//        override def fromBinary(bytes: Array[Byte]): Originator = {
//            Originator(
//                DeserializeOps.fromBinary(bytes),
//                DeserializeOps.fromBinary(bytes)
//            )
//        }
//    }
//
//    implicit object TransactionSerializer extends Serialize[Transaction] {
//        override def toBinary(value: Transaction): Array[Byte] = {
//            value match {
//                case rt: RawTransaction => {
//                    val origBytes = rt.orig.toBinary.toList
//                    val beneBytes = rt.bene.toBinary.toList
//                    val amountBytes = rt.amount.toBinary.toList
//                    val dCBytes = rt.debitCredit.toBinary.toList
//                    (origBytes ++ beneBytes ++ amountBytes ++ dCBytes).toArray[Byte]
//                }
//            }
//        }
//
//        override def fromBinary(bytes: Array[Byte]): Transaction = {
//            RawTransaction(
//                DeserializeOps.fromBinary(bytes),
//                DeserializeOps.fromBinary(bytes),
//                DeserializeOps.fromBinary(bytes),
//                DeserializeOps.fromBinary(bytes)
//            )
//        }
//    }
//
//    // 3. interface (enrichment and object)
//
//    implicit class SerializeOps[T](value: T) {
//        def toBinary[T](implicit serializer: Serialize[T]): Array[Byte] = {
//            serializer.toBinary(value)
//        }
//    }
//
//    object DeserializeOps {
//        def fromBinary[T](bytes: Array[Byte])(implicit serializer: Serialize[T]): T = {
//            serializer.fromBinary(bytes)
//        }
//    }
//
//
//    ///////////// Helper Functions //////////////////////
//
//    private def getDataStreamFor(bytes: Array[Byte]): DataInputStream = {
//        val bis = new ByteArrayInputStream(bytes)
//        val dis = new DataInputStream(bis)
//        dis
//    }
//}
