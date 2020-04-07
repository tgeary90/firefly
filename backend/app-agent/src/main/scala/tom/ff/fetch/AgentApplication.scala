package tom.ff.fetch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(
    basePackages = Array(
        "tom.ff.fetch"
    )
)
class AppConfig() {

}

object AgentApplication extends App {
    SpringApplication.run(classOf[AppConfig])
}
