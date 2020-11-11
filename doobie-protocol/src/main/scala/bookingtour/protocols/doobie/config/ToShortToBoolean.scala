package bookingtour.protocols.doobie.config

import cats.instances.short._
import cats.syntax.order._
import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2019.
  */
trait ToShortToBoolean {
  implicit final val shortToBooleanG: Get[Boolean] = Get[Short].map(_ === 1)
  implicit final val shortToBooleanP: Put[Boolean] = Put[Short].contramap { a =>
    if (a) {
      1
    } else
      0
  }
}
