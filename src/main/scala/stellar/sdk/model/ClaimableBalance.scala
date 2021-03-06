package stellar.sdk.model

import java.time.Instant

import okio.ByteString
import org.json4s.{DefaultFormats, Formats, JObject}
import stellar.sdk.model.ClaimableBalance.parseClaimableBalance
import stellar.sdk.model.response.ResponseParser
import stellar.sdk.{KeyPair, PublicKeyOps}

case class ClaimableBalance(
  id: ClaimableBalanceId,
  amount: Amount,
  sponsor: PublicKeyOps,
  claimants: List[Claimant],
  lastModifiedLedger: Long,
  lastModifiedTime: Instant
)

object ClaimableBalance {
  implicit val formats: Formats = DefaultFormats + ClaimantDeserializer

  def parseClaimableBalance(o: JObject): ClaimableBalance = {
    val idString = (o \ "id").extract[String]
    val decoded = ByteString.decodeHex(idString).toByteArray
    val value = ClaimableBalanceId.decode.run(decoded).value
    ClaimableBalance(
      id = value._2,
      amount = Amount.parseAmount(o),
      sponsor = KeyPair.fromAccountId((o \ "sponsor").extract[String]),
      claimants = (o \ "claimants").extract[List[Claimant]],
      lastModifiedLedger = (o \ "last_modified_ledger").extract[Long],
      lastModifiedTime = Instant.parse((o \ "last_modified_time").extract[String])
    )
  }
}

object ClaimableBalanceDeserializer extends ResponseParser[ClaimableBalance](parseClaimableBalance)