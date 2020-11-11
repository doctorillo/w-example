package bookingtour.protocols.business.rules.env

import java.util.UUID

import bookingtour.protocols.business.rules.processing.SegmentMeta
import bookingtour.protocols.core.values.enumeration.ContextItem
import cats.data.NonEmptyChain
import zio.ZIO
import zio.macros.access.accessible

/**
  * © Alexey Toroshchin 2019.
  */
@accessible
trait ServiceSegmentVector extends Serializable {
  val serviceSegmentVector: ServiceSegmentVector.Service[Any]
}

object ServiceSegmentVector {
  trait Service[R] {
    def build(
        partyId: UUID,
        ctx: ContextItem
    ): ZIO[R, String, List[SegmentMeta]]

    def fetchProviderGroup(xs: NonEmptyChain[SegmentMeta]): ZIO[Any, String, List[UUID]]
  }
}
