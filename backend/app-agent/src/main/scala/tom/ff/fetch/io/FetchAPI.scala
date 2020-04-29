package tom.ff.fetch.io

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PostMapping, RestController}
import tom.ff.fetch.domain.FetchTypes.Bucket
import tom.ff.fetch.service.{BucketService, PollingService}

@RestController("/buckets")
class FetchAPI {

  @Autowired
  var bucketService: BucketService = _

  @Autowired
  var pollingService: PollingService = _

  @PostMapping
  def invokeETL(): Unit = {
    pollingService.start
  }

  @GetMapping
  def getBuckets(): Seq[Bucket] = {
    bucketService.getBuckets
  }
}
