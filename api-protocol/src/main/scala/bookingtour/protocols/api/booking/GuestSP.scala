package bookingtour.protocols.api.booking

import java.util.UUID

import cats.instances.boolean._
import cats.instances.int._
import cats.instances.int._
import cats.instances.option._
import cats.instances.option._
import cats.instances.string._
import cats.instances.string._
import cats.instances.uuid._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class GuestSP(
    id: UUID,
    isAdult: Boolean,
    age: Option[Int] = None,
    boarding: Option[String] = None
)
