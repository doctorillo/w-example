package bookingtour.protocols.actors.aggregators

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate2Fn[-Channel0, -Channel1, +State] {
  def map(
      a: List[Channel0],
      b: List[Channel1]
  ): List[State]
}
