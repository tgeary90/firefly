package tom.ff.fetch.io

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RequestMethod, ResponseBody}

@Controller
class UIController {

    @GetMapping(Array("/"))
    def home(model: java.util.Map[String, Any]): String = {
      model.put("message", "welcome to firefly - ETL from the cloud!")
      "index.html"
    }
}
