package bookingtour.protocols.core.newtypes

import java.util.UUID

import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
package object idents {

  @newtype final case class SyncId(x: UUID)
  object SyncId {
    implicit val a: UUID => SyncId = x => SyncId(x)
    implicit val b: SyncId => UUID = _.x
  }

  @newtype final case class DataId(x: UUID)
  object DataId {
    implicit val a: UUID => DataId = x => DataId(x)
    implicit val b: DataId => UUID = _.x
  }

  @newtype final case class LabelId(x: UUID)
  object LabelId {
    implicit val a: UUID => LabelId = x => LabelId(x)
    implicit val b: LabelId => UUID = _.x
  }

  @newtype final case class EnumId(x: UUID)
  object EnumId {
    implicit val a: UUID => EnumId = x => EnumId(x)
    implicit val b: EnumId => UUID = _.x
  }

  @newtype final case class EnumValue(x: Int)
  object EnumValue {
    implicit val a: Int => EnumValue = x => EnumValue(x)
    implicit val b: EnumValue => Int = _.x
  }

  @newtype final case class DescriptionId(x: UUID)
  object DescriptionId {
    implicit val a: UUID => DescriptionId = x => DescriptionId(x)
    implicit val b: DescriptionId => UUID = _.x
  }

  @newtype final case class DataCodeId(x: UUID)
  object DataCodeId {
    implicit val a: UUID => DataCodeId = x => DataCodeId(x)
    implicit val b: DataCodeId => UUID = _.x
  }

  @newtype final case class DataValueCodeId(x: UUID)
  object DataValueCodeId {
    implicit val a: UUID => DataValueCodeId = x => DataValueCodeId(x)
    implicit val b: DataValueCodeId => UUID = _.x
  }

  @newtype final case class DataValueId(x: UUID)
  object DataValueId {
    implicit val a: UUID => DataValueId = x => DataValueId(x)
    implicit val b: DataValueId => UUID = _.x
  }

  @newtype final case class DataValue(x: UUID)
  object DataValue {
    implicit val a: UUID => DataValue = x => DataValue(x)
    implicit val b: DataValue => UUID = _.x
  }

  @newtype final case class ImageId(x: UUID)
  object ImageId {
    implicit val a: UUID => ImageId = x => ImageId(x)
    implicit val b: ImageId => UUID = _.x
  }

}
