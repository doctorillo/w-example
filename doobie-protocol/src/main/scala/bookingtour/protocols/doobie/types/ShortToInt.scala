package bookingtour.protocols.doobie.types

import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2019.
  */
object ShortToInt {
  trait ToMetaOps {
    implicit final val shortToIntG: Get[Int] = Get[Short].map(_.toInt)
    implicit final val shortToIntP: Put[Int] = Put[Short].contramap(_.toShort)
  }
  object mapping extends ToMetaOps
}
