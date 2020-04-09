package tom.ff.fetch.service

import org.springframework.stereotype.Component

@Component
class BucketService {
  def deleteBucket(id: String): Boolean = ???
  def addBucket(bucketUrl: String): Boolean = ???
}
