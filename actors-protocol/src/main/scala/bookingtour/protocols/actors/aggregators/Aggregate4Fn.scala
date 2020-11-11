package bookingtour.protocols.actors.aggregators

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate4Fn[-Channel0, -Channel1, -Channel2, -Channel3, +State] {
  def map(
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      d: List[Channel3]
  ): List[State]
}
