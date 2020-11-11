package bookingtour.protocols.core.register

import shapeless.HList

/**
  * © Alexey Toroshchin 2019.
  */
trait MetaRegister[L <: HList] {
  val entities: L
  def find[T]: Option[MetaEntity[T]]
}
