package stellar.sdk.model.response

import org.apache.commons.codec.binary.Base64
import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JArray, JObject}
import stellar.sdk._
import stellar.sdk.model.Amount.toBaseUnits
import stellar.sdk.model._

case class AccountResponse(id: PublicKey,
                           lastSequence: Long,
                           subEntryCount: Int,
                           thresholds: Thresholds,
                           authRequired: Boolean,
                           authRevocable: Boolean,
                           balances: List[Balance],
                           signers: List[Signer],
                           data: Map[String, String]) {

  def toAccount: Account = Account(id, lastSequence + 1)
}

object AccountRespDeserializer extends ResponseParser[AccountResponse]({ o: JObject =>
  implicit val formats = DefaultFormats
  val id = KeyPair.fromAccountId((o \ "id").extract[String])
  val seq = (o \ "sequence").extract[String].toLong
  val subEntryCount = (o \ "subentry_count").extract[Int]
  val lowThreshold = (o \ "thresholds" \ "low_threshold").extract[Int]
  val mediumThreshold = (o \ "thresholds" \ "med_threshold").extract[Int]
  val highThreshold = (o \ "thresholds" \ "high_threshold").extract[Int]
  val authRequired = (o \ "flags" \ "auth_required").extract[Boolean]
  val authRevocable = (o \ "flags" \ "auth_revocable").extract[Boolean]
  val JArray(jsBalances) = o \ "balances"
  val balances = jsBalances.map {
    case balObj: JObject =>
      val units = toBaseUnits((balObj \ "balance").extract[String].toDouble).get
      val amount = (balObj \ "asset_type").extract[String] match {
        case "credit_alphanum4" =>
          Amount(units, IssuedAsset4(
            code = (balObj \ "asset_code").extract[String],
            issuer = KeyPair.fromAccountId((balObj \ "asset_issuer").extract[String])
          ))
        case "credit_alphanum12" =>
          Amount(units, IssuedAsset12(
            code = (balObj \ "asset_code").extract[String],
            issuer = KeyPair.fromAccountId((balObj \ "asset_issuer").extract[String])
          ))
        case "native" => NativeAmount(units)
        case t => throw new RuntimeException(s"Unrecognised asset type: $t")
      }
      val limit = (balObj \ "limit").extractOpt[String].map(BigDecimal(_)).map(toBaseUnits).map(_.get)
      val buyingLiabilities = toBaseUnits(BigDecimal((balObj \ "buying_liabilities").extract[String])).get
      val sellingLiabilities = toBaseUnits(BigDecimal((balObj \ "selling_liabilities").extract[String])).get
      Balance(amount, limit, buyingLiabilities, sellingLiabilities)
    case _ => throw new RuntimeException(s"Expected js object at 'balances'")
  }
  val JArray(jsSigners) = o \ "signers"
  val signers = jsSigners.map {
    case signerObj: JObject =>
      val key = StrKey.decodeFromString((signerObj \ "key").extract[String]).asInstanceOf[SignerStrKey]
      val weight = (signerObj \ "weight").extract[Int]
      Signer(key, weight)
    case _ => throw new RuntimeException(s"Expected js object at 'signers'")
  }
  val JObject(dataFields) = o \ "data"
  val data = dataFields.toMap.mapValues(_.extract[String]).mapValues(Base64.decodeBase64).mapValues(new String(_))

  AccountResponse(id, seq, subEntryCount, Thresholds(lowThreshold, mediumThreshold, highThreshold), authRequired,
    authRevocable, balances, signers, data)

})