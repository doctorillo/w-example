package bookingtour.protocols.doobie.values.parties

import cats.instances.double._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.postgres.implicits._
import doobie.util.{Get, Put}
import org.postgresql.geometric.PGpoint
import tofu.logging.derivation.loggable

/**
  * Created by d0ct0r on 2019-10-26.
  */
@derive(encoder, decoder, order, loggable)
final case class GPoint(lng: Double, lat: Double)

object GPoint {
  final val Prague: GPoint         = GPoint(lng = 14.4378005, lat = 50.0755381)
  final val KarlovyVary: GPoint    = GPoint(lng = 12.871962, lat = 50.231852)
  final val MarianskeLazne: GPoint = GPoint(lng = 12.70118, lat = 49.96459)
  final val Jachimov: GPoint       = GPoint(lng = 12.934667, lat = 50.358473)
  final val Teplice: GPoint        = GPoint(lng = 13.835284, lat = 50.644458)

  implicit final val gpointG: Get[GPoint] = Get[PGpoint].map(p => GPoint(p.x, p.y))
  implicit final val gpointP: Put[GPoint] = Put[PGpoint].contramap(p => new PGpoint(p.lng, p.lat))
}
