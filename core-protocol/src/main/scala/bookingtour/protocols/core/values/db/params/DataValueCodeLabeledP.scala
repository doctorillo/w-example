package bookingtour.protocols.core.values.db.params

/**
  * © Alexey Toroshchin 2019.
  */
final case class DataValueCodeLabeledP(
    key: DataValueKey,
    code: String,
    labels: List[LabelP]
)

object DataValueCodeLabeledP {
  type Id = DataValueKey

  import DataValueKey.order._
  import bookingtour.protocols.core.types.CompareOps.compareFn
  import cats.Order
  import cats.instances.list._
  import cats.syntax.order._

  implicit final val dataValueCodeLabeledPR: DataValueCodeLabeledP => Id = _.key

  implicit final val dataValueCodeLabeledPO: Order[DataValueCodeLabeledP] =
    (x: DataValueCodeLabeledP, y: DataValueCodeLabeledP) =>
      compareFn(
        x.key.compare(y.key),
        x.code.compareTo(y.code),
        x.labels.compare(y.labels)
      )
}
