import com.google.inject.AbstractModule
import play.api.{Configuration, Environment}

import javax.inject._

/**
  * Sets up custom components for Play.
  *
  * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
  */
class Module(environment: Environment, configuration: Configuration)
    extends AbstractModule
    with ScalaModule {

  override def configure() = {
    bind[BucketRepository].to[BucketRepositoryImpl].in[Singleton]()
  }
}
