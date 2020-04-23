package tom.ff.etl.io

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.{FeatureSpec, FlatSpec, FunSuite}
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.{ContextConfiguration, TestContextManager}
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader

@SpringBootTest
@ContextConfiguration(
  loader = classOf[AnnotationConfigContextLoader])
class SampleTest extends FunSuite {

  new TestContextManager(this.getClass()).prepareTestInstance(this)

  test("test1") {
    assert(false)
  }
}
