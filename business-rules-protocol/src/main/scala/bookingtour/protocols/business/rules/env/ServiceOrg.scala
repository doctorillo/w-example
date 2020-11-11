package bookingtour.protocols.business.rules.env

import bookingtour.protocols.business.rules.processing.SegmentMeta
import bookingtour.protocols.parties.newTypes.PartyId
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait ServiceOrg extends Serializable {
  val serviceOrg: ServiceOrg.Service[Any]
}

object ServiceOrg {
  trait Service[R] {
    def supplier(party: PartyId): ZIO[R, Nothing, List[SegmentMeta]]
  }
}
