package bookingtour.protocols.doobie.values.properties.interlook

import bookingtour.protocols.core.types.CompareOps

/**
  * © Alexey Toroshchin 2019.
  */
final case class QuotaObject(id: Int, propertyId: Int, roomTypeId: Int, roomCategoryId: Int)

object QuotaObject {
  type Id = Int
  import cats.Order
  import io.circe.derivation._
  import io.circe.{Decoder, Encoder}

  implicit final val quotaObjectEnc: Encoder[QuotaObject] = deriveEncoder
  implicit final val quotaObjectDec: Decoder[QuotaObject] = deriveDecoder

  implicit final val quotaObjectO: Order[QuotaObject] = (x: QuotaObject, y: QuotaObject) =>
    CompareOps.compareFn(
      x.id.compare(y.id),
      x.propertyId.compare(y.propertyId),
      x.roomTypeId.compare(y.roomTypeId),
      x.roomCategoryId.compare(y.roomCategoryId)
    )

  implicit final val quotaObjectR: QuotaObject => Id = _.id
}
