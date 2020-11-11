package bookingtour.protocols.core.newtypes

import cats.Order
import cats.syntax.either._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
package object values {
  @newtype final case class UserName(x: NonEmptyString)
  final object UserName {
    implicit val circeEnc: Encoder[UserName] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[UserName] =
      Decoder.decodeString.emap(x => UserName(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[UserName] =
      (x: UserName, y: UserName) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class UserPassword(x: NonEmptyString)
  final object UserPassword {
    implicit val circeEnc: Encoder[UserPassword] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[UserPassword] =
      Decoder.decodeString.emap(x => UserPassword(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[UserPassword] =
      (x: UserPassword, y: UserPassword) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class RoomTypeLabel(x: NonEmptyString)
  final object RoomTypeLabel {
    def fromString(x: String): RoomTypeLabel = RoomTypeLabel(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[RoomTypeLabel] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[RoomTypeLabel] =
      Decoder.decodeString.emap(x => RoomTypeLabel(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[RoomTypeLabel] =
      (x: RoomTypeLabel, y: RoomTypeLabel) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class RoomCategoryLabel(x: NonEmptyString)
  final object RoomCategoryLabel {
    def fromString(x: String): RoomCategoryLabel = RoomCategoryLabel(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[RoomCategoryLabel] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[RoomCategoryLabel] =
      Decoder.decodeString.emap(x => RoomCategoryLabel(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[RoomCategoryLabel] =
      (x: RoomCategoryLabel, y: RoomCategoryLabel) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class TariffLabel(x: NonEmptyString)
  final object TariffLabel {
    def fromString(x: String): TariffLabel = TariffLabel(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[TariffLabel] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[TariffLabel] =
      Decoder.decodeString.emap(x => TariffLabel(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[TariffLabel] =
      (x: TariffLabel, y: TariffLabel) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class BoardingLabel(x: NonEmptyString)
  final object BoardingLabel {
    def fromString(x: String): BoardingLabel = BoardingLabel(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[BoardingLabel] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[BoardingLabel] =
      Decoder.decodeString.emap(x => BoardingLabel(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[BoardingLabel] =
      (x: BoardingLabel, y: BoardingLabel) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class PropertyDescription(x: NonEmptyString)
  final object PropertyDescription {
    def fromString(x: String): PropertyDescription =
      PropertyDescription(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[PropertyDescription] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[PropertyDescription] =
      Decoder.decodeString.emap(x => PropertyDescription(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[PropertyDescription] =
      (x: PropertyDescription, y: PropertyDescription) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class PropertyPaymentTerm(x: NonEmptyString)
  final object PropertyPaymentTerm {
    def fromString(x: String): PropertyPaymentTerm =
      PropertyPaymentTerm(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[PropertyPaymentTerm] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[PropertyPaymentTerm] =
      Decoder.decodeString.emap(x => PropertyPaymentTerm(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[PropertyPaymentTerm] =
      (x: PropertyPaymentTerm, y: PropertyPaymentTerm) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class PropertyCancellationTerm(x: NonEmptyString)
  final object PropertyCancellationTerm {
    def fromString(x: String): PropertyCancellationTerm =
      PropertyCancellationTerm(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[PropertyCancellationTerm] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[PropertyCancellationTerm] =
      Decoder.decodeString.emap(x => PropertyCancellationTerm(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[PropertyCancellationTerm] =
      (x: PropertyCancellationTerm, y: PropertyCancellationTerm) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class PropertyTaxTerm(x: NonEmptyString)
  final object PropertyTaxTerm {
    def fromString(x: String): PropertyTaxTerm =
      PropertyTaxTerm(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[PropertyTaxTerm] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[PropertyTaxTerm] =
      Decoder.decodeString.emap(x => PropertyTaxTerm(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[PropertyTaxTerm] =
      (x: PropertyTaxTerm, y: PropertyTaxTerm) => x.x.value.compareTo(y.x.value)
  }

  @newtype final case class PropertyGuestTerm(x: NonEmptyString)
  final object PropertyGuestTerm {
    def fromString(x: String): PropertyGuestTerm =
      PropertyGuestTerm(NonEmptyString.unsafeFrom(x))
    implicit val circeEnc: Encoder[PropertyGuestTerm] =
      Encoder.encodeString.contramap(_.x.value)
    implicit val circeDec: Decoder[PropertyGuestTerm] =
      Decoder.decodeString.emap(x => PropertyGuestTerm(NonEmptyString.unsafeFrom(x)).asRight)
    implicit val catsO: Order[PropertyGuestTerm] =
      (x: PropertyGuestTerm, y: PropertyGuestTerm) => x.x.value.compareTo(y.x.value)
  }
}
