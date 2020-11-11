package bookingtour.protocols.interlook.source.properties

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{AdultOnExtraBed, AdultOnMain, ChildOnExtraBed, ChildOnMain}
import bookingtour.protocols.interlook.source.newTypes.LookAccommodationId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AccommodationEP(
    id: LookAccommodationId,
    code: String,
    adultOnMain: AdultOnMain,
    adultOnExb: AdultOnExtraBed,
    childOnMain: ChildOnMain,
    childOnExb: ChildOnExtraBed,
    hasInfant: Boolean,
    perRoom: Boolean,
    stamp: Instant
)

object AccommodationEP {
  type Id = LookAccommodationId

  implicit final val itemR0: AccommodationEP => Id = _.id

  implicit final val itemP0: AccommodationEP => Int = _ => 0

  final case class Output(
      id: Int,
      code: Option[String],
      adultOnMain: Short,
      adultOnExb: Short,
      childOnMain: Short,
      childOnExb: Short,
      hasInfant: Option[Boolean],
      perRoom: Option[Boolean],
      stamp: LocalDateTime
  )

  val outputTransform: Output => AccommodationEP = _.into[AccommodationEP]
    .withFieldComputed(_.id, x => LookAccommodationId(x.id))
    .withFieldComputed(_.code, _.code.getOrElse("NO CODE"))
    .withFieldComputed(_.adultOnMain, x => AdultOnMain(x.adultOnMain.toInt))
    .withFieldComputed(_.adultOnExb, x => AdultOnExtraBed(x.adultOnExb.toInt))
    .withFieldComputed(_.childOnMain, x => ChildOnMain(x.childOnMain.toInt))
    .withFieldComputed(_.childOnExb, x => ChildOnExtraBed(x.childOnExb.toInt))
    .withFieldComputed(_.hasInfant, _.hasInfant.contains(true))
    .withFieldComputed(_.perRoom, _.perRoom.contains(true))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  implicit final class AccommodationEPOps(private val self: AccommodationEP) {
    def adults: Int          = self.adultOnMain.x + self.adultOnExb.x
    def children: Int        = self.childOnMain.x + self.childOnExb.x
    def pax: Int             = adults + children
    def hasChildren: Boolean = children > 0
    def onMain: Int          = self.adultOnMain.x + self.childOnMain.x
    def onExb: Int           = self.adultOnExb.x + self.childOnExb.x
    def withoutBed: Int =
      if (self.hasInfant) { 1 }
      else { 0 }
  }
}
