package stellar.sdk.model.response

import java.nio.charset.StandardCharsets.UTF_8

import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.{Formats, NoTypeHints}
import org.specs2.mutable.Specification
import stellar.sdk._
import stellar.sdk.model.Amount.lumens
import stellar.sdk.model._

class AccountResponseSpec extends Specification with ArbitraryInput with DomainMatchers {

  implicit val formats: Formats = Serialization.formats(NoTypeHints) + AccountRespDeserializer

  "a sample account response document" should {
    "parse to an account response" >> {
      val doc =
        """{
          |  "_links": {
          |    "self": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62"
          |    },
          |    "transactions": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/transactions{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "operations": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/operations{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "payments": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/payments{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "effects": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/effects{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "offers": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/offers{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "trades": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/trades{?cursor,limit,order}",
          |      "templated": true
          |    },
          |    "data": {
          |      "href": "https://horizon.stellar.org/accounts/GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62/data/{key}",
          |      "templated": true
          |    }
          |  },
          |  "id": "GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62",
          |  "paging_token": "",
          |  "account_id": "GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62",
          |  "sequence": "56251530273100047",
          |  "subentry_count": 156,
          |  "thresholds": {
          |    "low_threshold": 1,
          |    "med_threshold": 5,
          |    "high_threshold": 10
          |  },
          |  "flags": {
          |    "auth_required": false,
          |    "auth_revocable": false
          |  },
          |  "balances": [
          |    {
          |      "balance": "333.2771622",
          |      "limit": "100000000000.0000000",
          |      "buying_liabilities": "0.0000283",
          |      "selling_liabilities": "572.0000000",
          |      "asset_type": "credit_alphanum4",
          |      "asset_code": "JPY",
          |      "asset_issuer": "GBVAOIACNSB7OVUXJYC5UE2D4YK2F7A24T7EE5YOMN4CE6GCHUTOUQXM",
          |      "is_authorized": true,
          |      "is_authorized_to_maintain_liabilities": false
          |    },
          |    {
          |      "balance": "0.0000001",
          |      "limit": "100000000000.0000000",
          |      "buying_liabilities": "0.0000000",
          |      "selling_liabilities": "2.0000000",
          |      "asset_type": "credit_alphanum4",
          |      "asset_code": "BTC",
          |      "asset_issuer": "GDXTJEK4JZNSTNQAWA53RZNS2GIKTDRPEUWDXELFMKU52XNECNVDVXDI"
          |    },
          |    {
          |      "balance": "2.8256257",
          |      "limit": "922337203685.4775807",
          |      "buying_liabilities": "0.0000000",
          |      "selling_liabilities": "0.0000000",
          |      "asset_type": "credit_alphanum4",
          |      "asset_code": "BTC",
          |      "asset_issuer": "GATEMHCCKCY67ZUCKTROYN24ZYT5GK4EQZ65JJLDHKHRUZI3EUEKMTCH",
          |      "is_authorized": false,
          |      "is_authorized_to_maintain_liabilities": true
          |    },
          |    {
          |      "balance": "38615.8026333",
          |      "limit": "100000000000.0000000",
          |      "buying_liabilities": "40.0005000",
          |      "selling_liabilities": "0.0000000",
          |      "asset_type": "credit_alphanum4",
          |      "asset_code": "CNY",
          |      "asset_issuer": "GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX",
          |      "is_authorized": false,
          |      "is_authorized_to_maintain_liabilities": false
          |    },
          |    {
          |      "balance": "16001.4653423",
          |      "limit": "100000000000.0000000",
          |      "buying_liabilities": "0.0000000",
          |      "selling_liabilities": "2.3000000",
          |      "asset_type": "credit_alphanum4",
          |      "asset_code": "EURT",
          |      "asset_issuer": "GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S",
          |      "is_authorized": true,
          |      "is_authorized_to_maintain_liabilities": true,
          |      "sponsor": "GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S"
          |    },
          |    {
          |      "balance": "19309.4481807",
          |      "buying_liabilities": "0.0000000",
          |      "selling_liabilities": "0.0000000",
          |      "asset_type": "native"
          |    }
          |  ],
          |  "signers": [
          |    {
          |      "weight": 1,
          |      "key": "GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62",
          |      "type": "ed25519_public_key"
          |    },
          |    {
          |      "weight": 2,
          |      "key": "GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S",
          |      "type": "ed25519_public_key",
          |      "sponsor": "GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX"
          |    }
          |  ],
          |  "data": {},
          |  "sponsor": "GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S",
          |  "num_sponsoring": 1,
          |  "num_sponsored": 2
          |}
          |""".stripMargin

      parse(doc).extract[AccountResponse] must beLike {
        case r: AccountResponse =>
          r.id mustEqual KeyPair.fromAccountId("GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62")
          r.lastSequence mustEqual 56251530273100047L
          r.subEntryCount mustEqual 156
          r.thresholds mustEqual Thresholds(1, 5, 10)
          r.balances must containTheSameElementsAs(Seq(
            Balance(lumens(19309.4481807)),
            Balance(
              amount = Amount(160014653423L, IssuedAsset4("EURT", KeyPair.fromAccountId("GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S"))),
              limit = Some(1000000000000000000L),
              sellingLiabilities = 23000000L,
              authorized = true,
              authorizedToMaintainLiabilities = true,
              sponsor = Some(KeyPair.fromAccountId("GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S"))
            ),
            Balance(
              amount = Amount(386158026333L, IssuedAsset4("CNY", KeyPair.fromAccountId("GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX"))),
              limit = Some(1000000000000000000L),
              buyingLiabilities = 400005000L
            ),
            Balance(
              amount = Amount(28256257L, IssuedAsset4("BTC", KeyPair.fromAccountId("GATEMHCCKCY67ZUCKTROYN24ZYT5GK4EQZ65JJLDHKHRUZI3EUEKMTCH"))),
              limit = Some(9223372036854775807L),
              authorizedToMaintainLiabilities = true
            ),
            Balance(
              amount = Amount(1L, IssuedAsset4("BTC", KeyPair.fromAccountId("GDXTJEK4JZNSTNQAWA53RZNS2GIKTDRPEUWDXELFMKU52XNECNVDVXDI"))),
              limit = Some(1000000000000000000L),
              sellingLiabilities = 20000000L
            ),
            Balance(
              amount = Amount(3332771622L, IssuedAsset4("JPY", KeyPair.fromAccountId("GBVAOIACNSB7OVUXJYC5UE2D4YK2F7A24T7EE5YOMN4CE6GCHUTOUQXM"))),
              limit = Some(1000000000000000000L),
              buyingLiabilities = 283,
              sellingLiabilities = 5720000000L,
              authorized = true
            )
          ))
          r.signers must haveSize(2)
          r.signers.head must beEquivalentTo(Signer(AccountId(KeyPair.fromAccountId("GBU6GMZZ2KTQ33CHNVPAWWEJ22ZHLYGBGO3LIBKNANXUMNEOFROZKO62").publicKey.toIndexedSeq), 1))
          r.signers(1) must beEquivalentTo(
            Signer(
              AccountId(KeyPair.fromAccountId("GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S").publicKey.toIndexedSeq),
              2,
              Some(KeyPair.fromAccountId("GAREELUB43IRHWEASCFBLKHURCGMHE5IF6XSE7EXDLACYHGRHM43RFOX"))
            )
          )
          r.sponsor must beSome(KeyPair.fromAccountId("GAP5LETOV6YIE62YAM56STDANPRDO7ZFDBGSNHJQIYGGKSMOZAHOOS2S"))
          r.reservesSponsored mustEqual 2
          r.reservesSponsoring mustEqual 1
      }
    }

    "an account response" should {
      "be convertible to an account" >> prop { ar: AccountResponse =>
        ar.toAccount mustEqual Account(AccountId(ar.id.publicKey), ar.lastSequence + 1)
      }

      "denote when memos are required" >> prop { ar: AccountResponse =>
        ar.copy(data = ar.data.updated("config.memo_required", "1".getBytes(UTF_8)))
          .isMemoRequired must beTrue
        ar.copy(data = ar.data.updated("config.memo_required", "0".getBytes(UTF_8)))
          .isMemoRequired must beFalse
        ar.copy(data = ar.data.removed("config.memo_required"))
          .isMemoRequired must beFalse
      }
    }
  }

}
