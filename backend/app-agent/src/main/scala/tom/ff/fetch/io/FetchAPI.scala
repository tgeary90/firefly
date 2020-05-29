package tom.ff.fetch.io

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{DeleteMapping, GetMapping, PostMapping, RequestParam, RestController}
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

  // TODO query param eg. ?provider=gcp
  @GetMapping
  def getBuckets(@RequestParam provider: String): Seq[Bucket] = {
    bucketService.getBucketsFor(provider)
  }
}
