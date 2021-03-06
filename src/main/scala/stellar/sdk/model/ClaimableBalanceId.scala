package stellar.sdk.model

import cats.data.State
import okio.ByteString
import stellar.sdk.model.xdr.{Decode, Encodable, Encode}

sealed trait ClaimableBalanceId extends Encodable {
  def encodeString: String
}

object ClaimableBalanceId extends Decode {
  def decode: State[Seq[Byte], ClaimableBalanceId] = switch[ClaimableBalanceId](
    widen(bytes(32).map(_.toArray).map(new ByteString(_)).map(ClaimableBalanceHashId))
  )
}

case class ClaimableBalanceHashId(hash: ByteString) extends ClaimableBalanceId {
  override def encode: LazyList[Byte] = {
    val array = hash.toByteArray
    Encode.int(0) ++ Encode.bytes(32, array)
  }

  override def encodeString: String = new ByteString(encode.toArray).hex()
}
