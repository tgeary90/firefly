package tom.ff.etl.io

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http._
import tom.ff.etl.domain.ETLTypes._
import com.sksamuel.elastic4s.http.ElasticDsl._


object ElasticClientFactory extends LoadClientFactory {

  def client(host: String) = ElasticClient(ElasticProperties(s"http://${host}:9200"))

  def loadWithClient(client: ElasticClient)
                    (metadata: JobMetadata, txns: Seq[ValidatedTransaction]): Result[Seq[LoadResponse]] = {

    val isExists = client.execute {
      indexExists(metadata.provider)
    }.await.result.exists

    if ( ! isExists) {
      client.execute {
        createIndex(metadata.provider)
      }
    }

    val responses = txns.map { t =>
      val resp = client.execute {
        indexInto(metadata.provider / "transactions")
          .fields(
            "beneficiary.accNo" -> t.bene.accNo,
            "beneficiary.name"          -> t.bene.name,
            "originator.accNo"          -> t.orig.accNo,
            "originator.name"           ->t.orig.name,
            "amount"                    -> t.amount.quantity,
            "currency"                  -> t.amount.currency,
            "debitCredit"               -> t.debitCredit.toString
          ).refresh(RefreshPolicy.Immediate)
      }.await.result

      LoadResponse(resp.id)
    }
    Result(responses)
  }

  override def getLoaderFlow(host: String): Load = loadWithClient(client(host)) _
}
