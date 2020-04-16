package tom.ff.fetch.io

import javax.websocket.server.PathParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{DeleteMapping, PostMapping, RestController}
import tom.ff.fetch.service.BucketService

@RestController("/buckets")
class FetchAPI {

  @Autowired
  var bucketService: BucketService = _

  @PostMapping
  def createBucket(bucketUrl: String): Unit = {
    bucketService.addBucket(bucketUrl)
  }

  @DeleteMapping(Array("/buckets/{id}"))
  def deleteBucket(@PathParam("id") id: String): Unit = {
    bucketService.deleteBucket(Integer.parseInt(id))
  }
}
