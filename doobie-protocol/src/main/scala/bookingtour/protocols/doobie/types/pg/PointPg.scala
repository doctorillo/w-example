package bookingtour.protocols.doobie.types.pg

import cats.Order
import cats.instances.int._
import cats.syntax.order._
import org.postgresql.geometric.PGpoint

/**
  * © Alexey Toroshchin 2019.
  */
object PointPg {
  trait ToOrderOps {
    implicit final val pgPointO: Order[PGpoint] = (x: PGpoint, y: PGpoint) => {
      val _x = x.x.compareTo(y.x)
      val _y = x.y.compareTo(y.y)
      if (_x =!= 0) {
        _x
      } else {
        _y
      }
    }
  }

  final object order extends ToOrderOps
}
