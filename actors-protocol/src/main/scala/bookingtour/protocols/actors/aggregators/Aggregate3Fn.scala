package bookingtour.protocols.actors.aggregators

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate3Fn[-Channel0, -Channel1, -Channel2, +State] {
  def map(
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2]
  ): List[State]
}
