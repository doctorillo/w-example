package bookingtour.protocols.core.messages

import java.util.UUID

import bookingtour.protocols.core.register.RegisterEntity
import bookingtour.protocols.core.types.CompareOps
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
final case class TaggedChannelMeta[A](
    id: Option[UUID],
    tag: String,
    register: RegisterEntity.Aux[A]
)

object TaggedChannelMeta {
  import cats.instances.option._
  import cats.instances.uuid._
  import cats.syntax.order._

  implicit final def taggedChannelMeta[A]: Order[TaggedChannelMeta[A]] =
    (x: TaggedChannelMeta[A], y: TaggedChannelMeta[A]) =>
      CompareOps.compareFn(
        x.id.compare(y.id),
        x.tag.compareTo(y.tag),
        x.register.key.compare(y.register.key)
      )
}
