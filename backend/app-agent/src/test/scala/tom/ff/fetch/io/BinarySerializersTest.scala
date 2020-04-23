package tom.ff.fetch.io

import java.nio.charset.StandardCharsets

import org.scalatest.flatspec.AnyFlatSpec
import BinarySerializers._
import org.scalatest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.test.context.support.AnnotationConfigContextLoader
import tom.ff.fetch.domain.Types.Transaction

@SpringBootTest
@ContextConfiguration(
  classes = Array(classOf[Config]),
  loader = classOf[AnnotationConfigContextLoader])
class BinarySerializersTest extends AnyFlatSpec {

  var appContext: ApplicationContext = null

  @Autowired
  def applicationContext(applicationContext: ApplicationContext) = {
    this.appContext = applicationContext
  }

  new TestContextManager(this.getClass()).prepareTestInstance(this)

  "BinarySerializer" should "deserialize a 'HelloWorld' String" in {
    val bytes: Array[Byte] = Array(104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 10)

    val hi: String = new String(bytes, StandardCharsets.UTF_8)

    assert(hi  == "hello, worlds")
  }

  "BinarySerializer" should "deserialize a Transaction" in {
    val bytes: Array[Byte] = Array(49,50,51,52,53,54,55,56,44,34,116,111,109,32,103,101,97,114,121,34,44,56,55,54,53,52,51,50,49,44,34,109,105,99,107,101,121,32,106,46,32,109,111,117,115,101,34,44,49,52,46,55,50,44,100,10)

    val t = DeserializeOps.fromBinary[Transaction](bytes)

    assert(t !=  null)
  }
}
