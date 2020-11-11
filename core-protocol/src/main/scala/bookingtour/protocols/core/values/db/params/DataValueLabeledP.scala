package bookingtour.protocols.core.values.db.params

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class DataValueLabeledP(dataId: UUID, valueId: UUID, labels: List[LabelP])
