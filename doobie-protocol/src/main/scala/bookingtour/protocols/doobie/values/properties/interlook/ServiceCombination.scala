package bookingtour.protocols.doobie.values.properties.interlook

import bookingtour.protocols.core.types.CompareOps.compareFn
import cats.Order
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * Created by d0ct0r on 2019-11-08.
  */
final case class ServiceCombination(id: Int, propertyId: Int, roomTypeId: Int, roomCategoryId: Int)

object ServiceCombination {
  type Id = Int

  implicit final val serviceCombinationR: ServiceCombination => Id = _.id

  implicit final val serviceCombinationEnc: Encoder[ServiceCombination] = deriveEncoder
  implicit final val serviceCombinationDec: Decoder[ServiceCombination] = deriveDecoder

  implicit final val serviceCombinationO: Order[ServiceCombination] =
    (x: ServiceCombination, y: ServiceCombination) =>
      compareFn(
        x.id.compare(y.id),
        x.propertyId.compare(y.propertyId),
        x.roomTypeId.compare(y.roomTypeId),
        x.roomCategoryId.compare(y.roomCategoryId)
      )
}
