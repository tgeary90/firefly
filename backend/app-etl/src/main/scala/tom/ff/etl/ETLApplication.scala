package tom.ff.etl

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan(
  basePackages = Array(
    "tom.ff.etl"
  )
)
class AppConfig() {

}

object ETLApplication extends App {
    SpringApplication.run(classOf[AppConfig])
}
