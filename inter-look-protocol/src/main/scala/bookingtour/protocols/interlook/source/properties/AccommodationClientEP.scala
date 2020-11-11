package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Pax
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.BedItem
import bookingtour.protocols.interlook.source.newTypes.{LookAccommodationAgeId, LookAccommodationId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AccommodationClientEP(
    id: LookAccommodationAgeId,
    accommodation: LookAccommodationId,
    age: Ranges.Ints,
    pax: Pax,
    bed: BedItem
)

object AccommodationClientEP {
  final type Id = LookAccommodationAgeId

  implicit final val itemR: AccommodationClientEP => LookAccommodationAgeId = _.id

  implicit final val itemP: AccommodationClientEP => Int = _ => 0

  final case class Output(
      id: Int,
      accommodationId: Int,
      ageFrom: Int,
      ageTo: Int,
      nmen: Short,
      onMain: Option[Boolean]
  )

  val outputTransform: Output => AccommodationClientEP = _.into[AccommodationClientEP]
    .withFieldComputed(_.id, x => LookAccommodationAgeId(x.id))
    .withFieldComputed(_.accommodation, x => LookAccommodationId(x.accommodationId))
    .withFieldComputed(_.pax, x => Pax(x.nmen.toInt))
    .withFieldComputed(_.age, x => Ranges.Ints(x.ageFrom, x.ageTo))
    .withFieldComputed(
      _.bed,
      x =>
        if (x.onMain.contains(true)) {
          BedItem.Main
        } else {
          BedItem.Extra
        }
    )
    .transform

  /*final object instances {
    def adultOnMain: AccommodationClientEP =
      AccommodationClientEP(
        id = None,
        age = Ranges.adultAge,
        bed = BedItem.Main
      )
    def adultOnExb: AccommodationClientEP =
      AccommodationClientEP(
        id = None,
        age = Ranges.adultAge,
        bed = BedItem.Extra
      )
    def infant: AccommodationClientEP =
      AccommodationClientEP(
        id = None,
        age = Ranges.infantAge,
        bed = BedItem.Without
      )
  }*/
}
