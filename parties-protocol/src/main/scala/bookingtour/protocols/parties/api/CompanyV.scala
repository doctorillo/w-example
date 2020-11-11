package bookingtour.protocols.parties.api

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.instances.int._
import cats.instances.option._
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class CompanyV(uuid: UUID, id: Option[Int], name: String)

object CompanyV {
  implicit final val companyVEnc: Encoder[CompanyV] = deriveEncoder
  implicit final val companyVDec: Decoder[CompanyV] = deriveDecoder

  implicit final val companyVO: Order[CompanyV] = (x: CompanyV, y: CompanyV) =>
    CompareOps.compareFn(
      x.uuid.compareTo(y.uuid),
      x.id.compare(y.id),
      x.name.compareTo(y.name)
    )
}
