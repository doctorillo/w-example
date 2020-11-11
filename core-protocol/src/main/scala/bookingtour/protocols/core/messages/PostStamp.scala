package bookingtour.protocols.core.messages

import java.time.Instant

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.data.{Chain, NonEmptyList}
import cats.instances.string._
import cats.syntax.order._
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder)
final case class PostStamp(
    office: PostOffice,
    stamp: Instant = Instant.now(),
    errors: Chain[String] = Chain.empty
)

object PostStamp {
  implicit final val postStampO: Order[PostStamp] = (x: PostStamp, y: PostStamp) =>
    CompareOps.compareFn(
      x.office.compare(y.office),
      x.stamp.compareTo(y.stamp),
      x.errors.compare(y.errors)
    )
  implicit final val postStampOrdering: Ordering[PostStamp] = postStampO.toOrdering

  final def one(po: PostOffice): NonEmptyList[PostStamp] = NonEmptyList.one(PostStamp(office = po))
}
