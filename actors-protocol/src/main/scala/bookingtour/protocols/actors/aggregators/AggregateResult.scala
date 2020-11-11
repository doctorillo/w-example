package bookingtour.protocols.actors.aggregators

/**
  * © Alexey Toroshchin 2019.
  */
final case class AggregateResult[Value](
    created: List[Value],
    updated: List[Value],
    deleted: List[Value],
    state: List[Value]
)

object AggregateResult {
  final def empty[Value]: AggregateResult[Value] =
    AggregateResult[Value](
      created = List.empty,
      updated = List.empty,
      deleted = List.empty,
      state = List.empty
    )
}
