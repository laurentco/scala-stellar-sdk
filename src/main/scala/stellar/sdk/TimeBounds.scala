package stellar.sdk

import java.time.Instant

import org.stellar.sdk.xdr.{TimeBounds => XDRTimeBounds}
import stellar.sdk.XDRPrimitives._

case class TimeBounds(start: Instant, end: Instant) {
  assert(start.isBefore(end))

  def toXDR: XDRTimeBounds = {
    val tb = new XDRTimeBounds
    tb.setMinTime(uint64(start.toEpochMilli))
    tb.setMaxTime(uint64(end.toEpochMilli))
    tb
  }
}
