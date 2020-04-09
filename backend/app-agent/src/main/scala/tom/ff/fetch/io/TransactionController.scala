//package org.tom.firefly.fetch.api
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.web.bind.annotation._
//
//@RestController("/transaction")
//class TransactionController {
//
//    @Autowired
//    var metricsService: MetricsService = _
//
//    @Autowired
//    var gcpService: GCPService = _
//
//    @GetMapping
//    def getTransactionCount(@RequestParam metric: String): Int = {
//
//        metric match {
//            case "count" => metricsService.transactionCount
//            case _ => 0
//        }
//    }
//
//    @PostMapping
//    def checkForTransactions(): Int = {
//        val result: Result[FetchError, List[TransactionDTO]] = FetchAPI.fetch()
//
//        val maybeJob: Result[JobError, Job[TransactionDTO]] = result match {
//            case Left(err) => throw new RuntimeException(err.getLocalizedMessage)
//            case Right(dtos) =>  FetchAPI.createJob(dtos)
//        }
//
//        val enqueueResult = maybeJob match {
//            case Left(err) => throw new RuntimeException(err.getLocalizedMessage)
//            case Right(job) => FetchAPI.enqueue(job)
//        }
//
//        enqueueResult.right.get.toInt
//    }
//
//}
