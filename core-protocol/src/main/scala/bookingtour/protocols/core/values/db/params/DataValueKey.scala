package bookingtour.protocols.core.values.db.params

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class DataValueKey(dataId: UUID, valueId: UUID)

object DataValueKey {
  type Id = DataValueKey
  import bookingtour.protocols.core.types.CompareOps.compareFn
  import cats.Order
  import cats.data.Reader

  trait ToOrderOps {
    implicit final val dataValueKeyO: Order[DataValueKey] = (x: DataValueKey, y: DataValueKey) =>
      compareFn(
        x.dataId.compareTo(y.dataId),
        x.valueId.compareTo(y.valueId)
      )
  }

  final object order extends ToOrderOps

  trait ToReaderOps {
    implicit final val dataValueKeyR: Reader[DataValueKey, Id] = Reader(x => x)
  }

  final object reader extends ToReaderOps
}
