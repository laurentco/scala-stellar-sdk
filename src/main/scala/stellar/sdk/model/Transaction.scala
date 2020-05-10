package stellar.sdk.model

import cats.data._
import stellar.sdk.model.TimeBounds.Unbounded
import stellar.sdk.model.op.Operation
import stellar.sdk.model.response.TransactionPostResponse
import stellar.sdk.model.xdr.Encode.{arr, int, long, opt}
import stellar.sdk.model.xdr.{Decode, Encodable, Encode}
import stellar.sdk.util.ByteArrays
import stellar.sdk.util.ByteArrays._
import stellar.sdk.{KeyPair, Network, Signature}

import scala.concurrent.{ExecutionContext, Future}

case class Transaction(source: Account,
                       operations: Seq[Operation] = Nil,
                       memo: Memo = NoMemo,
                       timeBounds: TimeBounds,
                       maxFee: NativeAmount)(implicit val network: Network) extends Encodable {

  private val BaseFee = 100L
  private val EnvelopeTypeTx = 2

  def add(op: Operation): Transaction = this.copy(operations = operations :+ op)

  def minFee: NativeAmount = NativeAmount(operations.size * BaseFee)

  def sign(key: KeyPair, otherKeys: KeyPair*): SignedTransaction = {
    val h = hash.toArray
    val signatures = (key +: otherKeys).map(_.sign(h))
    SignedTransaction(this, signatures)
  }

  def sign(preImage: Seq[Byte]): SignedTransaction = {
    val signedPreImage = Signature(preImage.toArray, ByteArrays.sha256(preImage).drop(28))
    val signatures = List(signedPreImage)
    SignedTransaction(this, signatures)
  }

  def hash: Seq[Byte] = ByteArrays.sha256(network.networkId ++ Encode.int(EnvelopeTypeTx) ++ encode)
    .toIndexedSeq

  /**
    * The base64 encoding of the XDR form of this unsigned transaction.
    */
  def encodeXDR: String = base64(encode)

  // Encodes to TransactionV0 format by default for backwards compatibility with core protocol 12.
  // But if the accountId is muxed (has a sub-account id), then encode to TransactionV1 for protocol 13+
  def encode: LazyList[Byte] = if (source.id.isMulitplexed) encodeV1 else encodeV0

  def encodeV0: LazyList[Byte] = {
    source.id.copy(subAccountId = None).encode ++
      int(maxFee.units.toInt) ++
      long(source.sequenceNumber) ++
      opt(Some(timeBounds).filterNot(_ == Unbounded)) ++
      memo.encode ++
      arr(operations) ++
      int(0)
  }

  def encodeV1: LazyList[Byte] = {
    int(2) ++
      source.id.encode ++
      int(maxFee.units.toInt) ++
      long(source.sequenceNumber) ++
      opt(Some(timeBounds).filterNot(_ == Unbounded)) ++
      memo.encode ++
      arr(operations) ++
      int(0)
  }
}

object Transaction extends Decode {

  /**
    * Decodes an unsigned transaction from base64-encoded XDR.
    */
  def decodeXDR(base64: String)(implicit network: Network): Transaction =
    decode.run(ByteArrays.base64(base64).toIndexedSeq).value._2

  def decode(implicit network: Network): State[Seq[Byte], Transaction] = int.flatMap {
    case 0 => decodeV0
    case 2 => decodeV1
  }

  def decodeV0(implicit network: Network) = {
    for {
      publicKeyBytes <- bytes(32)
      accountId = AccountId(publicKeyBytes.toArray[Byte])
      fee <- int
      seqNo <- long
      timeBounds <- opt(TimeBounds.decode).map(_.getOrElse(Unbounded))
      memo <- Memo.decode
      ops <- arr(Operation.decode)
      _ <- int
    } yield Transaction(Account(accountId, seqNo), ops, memo, timeBounds, NativeAmount(fee))
  }

  def decodeV1(implicit network: Network) = {
    for {
      accountId <- StrKey.decode.map(_.asInstanceOf[AccountId])
      fee <- int
      seqNo <- long
      timeBounds <- opt(TimeBounds.decode).map(_.getOrElse(Unbounded))
      memo <- Memo.decode
      ops <- arr(Operation.decode)
      _ <- int
    } yield Transaction(Account(accountId, seqNo), ops, memo, timeBounds, NativeAmount(fee))
  }
}

case class SignedTransaction(transaction: Transaction,
                             signatures: Seq[Signature],
                             feeBump: Option[FeeBump] = None) {

  assert(transaction.minFee.units <= transaction.maxFee.units,
    "Insufficient maxFee. Allow at least 100 stroops per operation. " +
      s"[maxFee=${transaction.maxFee.units}, operations=${transaction.operations.size}].")

  def submit()(implicit ec: ExecutionContext): Future[TransactionPostResponse] = {
    transaction.network.submit(this)
  }

  def sign(key: KeyPair): SignedTransaction =
    this.copy(signatures = key.sign(transaction.hash.toArray) +: signatures)

  def sign(preImage: Seq[Byte]): SignedTransaction =
    this.copy(signatures = Signature(preImage.toArray, ByteArrays.sha256(preImage)) +: signatures)

  /**
    * The base64 encoding of the XDR form of this signed transaction.
    */
  def encodeXDR: String = base64(encode)

  def encode: LazyList[Byte] = transaction.encode ++ Encode.arr(signatures)
}

object SignedTransaction extends Decode {

  /**
    * Decodes a signed transaction (aka envelope) from base64-encoded XDR.
    */
  def decodeXDR(base64: String)(implicit network: Network): SignedTransaction =
    decode.run(ByteArrays.base64(base64).toIndexedSeq).value._2

  // TODO - add a discriminator here for v0, v1 or fee bump
  //  if the disc is 0, then it's an old-school txn which is actually starting with a 0 for TxnEnv -> Txn -> AccountId -> publicKey type

  def decode(implicit network: Network): State[Seq[Byte], SignedTransaction] = for {
    discriminator <- int
    txn <- discriminator match {
      // parse a legacy transaction, without the public key discriminator, into a standard transaction
      case 0 => Transaction.decodeV0

      // parse a standard transaction, with MuxedAccount
      case 2 => Transaction.decodeV1

//      case 5 => ??? // parse a fee bump transaction
    }
    sigs <- arr(Signature.decode)
  } yield SignedTransaction(txn, sigs)
}
