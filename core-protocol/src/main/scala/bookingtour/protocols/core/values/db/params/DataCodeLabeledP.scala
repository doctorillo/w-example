package bookingtour.protocols.core.values.db.params

import java.util.UUID

/**
  * © Alexey Toroshchin 2019.
  */
final case class DataCodeLabeledP(dataId: UUID, code: String, labels: List[LabelP])
