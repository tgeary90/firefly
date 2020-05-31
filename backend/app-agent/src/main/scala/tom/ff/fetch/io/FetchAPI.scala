package tom.ff.fetch.io

import java.io.StringWriter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PostMapping, RequestParam, ResponseBody, RestController}
import tom.ff.fetch.domain.FetchTypes.Bucket
import tom.ff.fetch.service.{BucketService, PollingService}

@RestController
class FetchAPI {

  @Autowired
  var bucketService: BucketService = _

  @Autowired
  var pollingService: PollingService = _

  @PostMapping
  def invokeETL(): Unit = {
    pollingService.start
  }

  @GetMapping(path = Array("/buckets"), produces = Array("application/json"))
  def getBuckets(@RequestParam provider: String): ResponseEntity[String] = {
    val buckets = bucketService.getBucketsFor(provider)

    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val out = new StringWriter()

    buckets.map {
      b => mapper.writeValue(out, b).toString
    }

    ResponseEntity.ok(out.toString)
  }
}
