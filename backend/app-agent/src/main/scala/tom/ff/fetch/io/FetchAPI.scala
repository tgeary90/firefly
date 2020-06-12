package tom.ff.fetch.io

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation._
import tom.ff.fetch.service.{BucketService, PollingService}

@RestController
//@CrossOrigin(origins=Array("http://localhost:4200"))
@CrossOrigin
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

    val bucketsJson = mapper.writeValueAsString(buckets)
    ResponseEntity.ok(bucketsJson)
  }
}
