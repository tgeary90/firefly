package tom.ff.etl.io

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http._
import tom.ff.etl.domain.ETLTypes._
import com.sksamuel.elastic4s.http.ElasticDsl._


object ElasticClientFactory extends LoadClientFactory {

  val client = ElasticClient(ElasticProperties("http://localhost:9200"))

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
        indexInto(s"{metadata.provider}" / "transactions")
          .fields(
            "beneficiary" -> t.bene,
            "originator" -> t.orig,
            "amount" -> t.amount,
            "debitCredit" -> t.debitCredit
          ).refresh(RefreshPolicy.Immediate)
      }.await.result

      LoadResponse(resp.id)
    }
    Result(responses)
  }

  override def getLoaderFlow(): Load = loadWithClient(client) _
}
