package bookingtour.protocols.core.types

/**
  * © Alexey Toroshchin 2019.
  */
trait Sign[T] {
  def reverse(x: T): T
}

object Sign {
  def apply[T](implicit ev: Sign[T]): Sign[T] = ev
}
