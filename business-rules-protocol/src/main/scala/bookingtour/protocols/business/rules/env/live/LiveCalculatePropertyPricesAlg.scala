package bookingtour.protocols.business.rules.env.live

import java.time.LocalDate

import scala.annotation.tailrec

import bookingtour.protocols.business.rules.env.CalculatePropertyPricesAlg
import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Age, Counter, Nights, Pax, Position, RuleModifier}
import bookingtour.protocols.core.newtypes.values.{BoardingLabel, RoomCategoryLabel, RoomTypeLabel, TariffLabel}
import bookingtour.protocols.core.types.Sign
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem, RuleApplyItem}
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.parties.alg.RelationOrg.TerminateCustomerOrg
import bookingtour.protocols.parties.api.queries.QueryGuest
import bookingtour.protocols.parties.api.queries.{QueryGuest, QueryRoom}
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import bookingtour.protocols.properties.agg.RoomUnitProduct
import bookingtour.protocols.properties.api.BoardingProduct
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.property.prices.api.{
  AccommodationGuestOp,
  OfferDateOp,
  OfferOp,
  OfferRuleVILP,
  PriceChunkUI,
  PriceOp,
  PriceUnitOp,
  PriceVariantUI,
  PropertyPriceCardProduct,
  StopSaleVILP,
  TariffOp,
  VariantOp
}
import cats.Order
import cats.instances.all._
import cats.kernel.Monoid
import cats.syntax.monoid._
import cats.syntax.option._
import cats.syntax.order._
import com.typesafe.scalalogging.Logger
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveCalculatePropertyPricesAlg private (enableTrace: Boolean) extends CalculatePropertyPricesAlg {
  implicit private val pco: Ordering[PriceChunkUI] = implicitly[Order[PriceChunkUI]].toOrdering
  implicitly[Order[PriceVariantUI]].toOrdering
  private val log: Logger = Logger[CalculatePropertyPricesAlg]

  val calculatePropertyPricesAlg: CalculatePropertyPricesAlg.Service[Any] =
    new CalculatePropertyPricesAlg.Service[Any] {
      private final def finalizeProcessor(price: PriceVariantUI): Option[PriceVariantUI] = {
        def go() = {
          if (!price.chunks.exists(_.rules.nonEmpty)) {
            None
          } else {
            price.chunks.flatMap(_.rules.toList).headOption match {
              case None =>
                None
              case Some(rule) if rule.modifier != RuleModifier(0) =>
                rule.applyTo match {
                  case RuleApplyItem.LastNight =>
                    Amount
                      .multiply(price.chunks.last.price, rule.modifier.x)
                      .some

                  case RuleApplyItem.MinPrice =>
                    Amount
                      .multiply(price.chunks.min.price, rule.modifier.x)
                      .some

                  case RuleApplyItem.MaxPrice =>
                    Amount
                      .multiply(price.chunks.max.price, rule.modifier.x)
                      .some

                  case _ =>
                    None
                }
              case Some(rule) =>
                if (enableTrace) {
                  log.info(s"modifier for ${rule.sync} === 0")
                }
                None
            }
          }
        }
        val dateValid = Monoid[Nights].combineAll(price.chunks.map(_.nights)) === price.nights
        if (!dateValid) {
          none[PriceVariantUI]
        } else {
          price.currency match {
            case CurrencyItem.Euro =>
              val amount =
                Monoid[Amount.Euro].combineAll(price.chunks.map(_.amount.asInstanceOf[Amount.Euro]))
              val discount = go().map(_.asInstanceOf[Amount.Euro])
              val totalOpt = amount.some |+| discount.map(Sign[Amount.Euro].reverse(_))
              totalOpt.map(x => price.copy(amount = amount, discount = discount, total = x))

            case CurrencyItem.Czk =>
              val amount =
                Monoid[Amount.Czk].combineAll(price.chunks.map(_.amount.asInstanceOf[Amount.Czk]))
              val discount = go().map(_.asInstanceOf[Amount.Czk])
              val totalOpt = amount.some |+| discount.map(Sign[Amount.Czk].reverse(_))
              totalOpt.map(x => price.copy(amount = amount, discount = discount, total = x))
          }
        }
      }

      private final def dateProcessor(
          now: LocalDate,
          left: Ranges.Dates,
          right: Ranges.Dates
      ): (Ranges.Dates, Int) = {
        val newNow = if (left.to.isBefore(right.to)) {
          left.to
        } else {
          right.to
        }
        val newDates = Ranges.Dates(now, newNow)
        val nights   = newDates.daysInclusive
        (newDates, nights)
      }

      @tailrec
      private final def chunkProcessor(
          dates: Ranges.Dates,
          now: LocalDate,
          actual: List[PriceChunkUI],
          source: List[PriceChunkUI]
      ): List[PriceChunkUI] = {
        if (dates.to.isBefore(now) || source.isEmpty) {
          actual
        } else {
          val (spo, ordinal) = source
            .filter(x => x.dates.dateInRange(now) && x.stayDuration.contains(dates.daysInclusive))
            .partition(_.specialOffer)
          spo.sorted match {
            case Nil =>
              ordinal.sorted match {
                case Nil =>
                  List.empty

                case head :: _ =>
                  val (newDates, nights) = dateProcessor(now, head.dates, dates)
                  chunkProcessor(
                    dates,
                    newDates.to.plusDays(1),
                    actual :+ head.copy(
                      dates = newDates,
                      nights = Nights(nights),
                      rules = head.rules.filter(_.input.contains(nights)),
                      amount = Amount.multiply(head.price, nights)
                    ),
                    source
                  )
              }

            case head :: _ =>
              val (newDates, nights) = dateProcessor(now, head.dates, dates)
              chunkProcessor(
                dates,
                newDates.to.plusDays(1),
                actual :+ head.copy(
                  dates = newDates,
                  nights = Nights(nights),
                  rules = head.rules.filter(_.input.contains(nights)),
                  amount = Amount.multiply(head.price, nights)
                ),
                source
              )
          }
        }
      }

      private final def chunks(
          supplierId: PartyId,
          groupId: CustomerGroupId,
          dates: Ranges.Dates,
          tariff: TariffOp,
          offers: List[OfferOp],
          offerRules: List[OfferRuleVILP],
          priceDates: List[OfferDateOp],
          priceUnits: PriceUnitOp,
          prices: List[PriceOp]
      ): List[PriceChunkUI] = {
        val xs = for {
          pd <- priceDates
          _  <- pd.dates.intersect(dates).toList
          o  <- offers.find(_.id === pd.offer).toList
          r  = offerRules.filter(_.offer === o.id)
          p <- prices.filter(x =>
                x.tariff === tariff.id && x.group === groupId && x.priceUnit === priceUnits.id && x.offerDate === pd.id
              )
        } yield PriceChunkUI(
          id = p.id,
          priceDateId = pd.id,
          offerId = o.id,
          tariffId = tariff.id,
          stayDuration = pd.duration,
          dates = pd.dates,
          rules = r,
          nights = Nights(0),
          price = p.amount,
          amount = Monoid[Amount.Euro].empty,
          discount = None,
          specialOffer = o.special
        )
        chunkProcessor(dates, dates.from, List.empty, xs)
      }

      private def guestCheck(x: List[AccommodationGuestOp], y: List[QueryGuest]): Boolean = {
        if (x.length =!= y.length) {
          false
        } else {
          x.foldLeft((true, y)) { (acc, x) =>
              if (!acc._1) {
                acc
              } else {
                acc._2.find(z => x.age.contains(z.age.getOrElse(Age.Adult))) match {
                  case None =>
                    (false, List.empty)
                  case Some(value) =>
                    (true, acc._2.filterNot(_ === value))
                }
              }
            }
            ._1
        }
      }
      private def roomVariant(
          lang: LangItem,
          currency: CurrencyItem,
          checkInDates: Ranges.Dates,
          guests: List[QueryGuest],
          propertyId: PropertyId,
          customerId: PartyId,
          customerOrg: TerminateCustomerOrg,
          room: QueryRoom,
          tariff: TariffOp,
          roomUnit: RoomUnitProduct,
          roomVariant: VariantOp,
          priceUnit: PriceUnitOp,
          boarding: BoardingProduct,
          offers: List[OfferOp],
          offerRules: List[OfferRuleVILP],
          priceDates: List[OfferDateOp],
          prices: List[PriceOp],
          stopSales: List[StopSaleVILP]
      ): List[PriceVariantUI] = {
        val supplierId = customerOrg.partyId
        val chxs = chunks(
          supplierId = supplierId,
          groupId = customerOrg.groupId,
          dates = checkInDates,
          tariff = tariff,
          offers = offers,
          offerRules = offerRules,
          priceDates = priceDates,
          priceUnits = priceUnit,
          prices = prices
        )
        for {
          _   <- chxs.headOption.toList
          rt  <- roomUnit.typeLabels.find(_.lang === lang).map(_.label).toList
          rc  <- roomUnit.categoryLabels.find(_.lang === lang).map(_.label).toList
          brd <- boarding.names.find(_.lang === lang).map(_.label).toList
          trf <- tariff.labels.find(_.lang === lang).map(_.label).toList
          ss  = stopSales.exists(_.target.units.contains(roomUnit.id))
          total <- finalizeProcessor(
                    PriceVariantUI(
                      lang = lang,
                      currency = currency,
                      propertyId = propertyId,
                      customerId = customerId,
                      supplierId = supplierId,
                      groupId = customerOrg.groupId,
                      checkInDates = checkInDates,
                      roomOrder = Position.Zero,
                      guests = guests,
                      priceUnitId = priceUnit,
                      variantId = roomVariant,
                      roomTypeId = roomUnit.typeId,
                      roomType = RoomTypeLabel.fromString(rt),
                      roomCategoryId = roomUnit.categoryId,
                      roomCategory = RoomCategoryLabel.fromString(rc),
                      boardingId = boarding.id,
                      boarding = BoardingLabel.fromString(brd),
                      tariffId = tariff,
                      tariff = TariffLabel.fromString(trf),
                      nights = Nights(checkInDates.daysInclusive),
                      pax = Pax(guests.length),
                      stopSale = ss,
                      resultCount = Counter(1),
                      chunks = chxs,
                      discount = None,
                      amount = Monoid[Amount.Euro].empty,
                      total = Monoid[Amount.Euro].empty
                    )
                  ).toList
        } yield total
      }

      def run(
          lang: LangItem,
          currency: CurrencyItem,
          dates: Ranges.Dates,
          groups: List[TerminateCustomerOrg],
          customerId: PartyId,
          rooms: List[QueryRoom],
          card: PropertyPriceCardProduct,
          prices: List[PriceOp],
          stopSales: List[StopSaleVILP]
      ): URIO[Any, List[PriceVariantUI]] = {
        val roomCompact  = rooms.map(_.copy(position = Position.Zero)).distinct
        val checkInDates = dates.copy(to = dates.to.minusDays(1L))
        ZIO
          .foreach(roomCompact) { x =>
            ZIO.effectTotal(
              (for {
                cg <- groups.find(x => x.partyId === card.supplier).toList
                //_ = log.info(s"${card.name}. 1. customer-group exist.")
                ru <- card.roomUnits.filter(_.propertyId === card.id)
                // _ = log.info(s"${card.name}. 2. room unit exist.")
                rv <- card.variants
                       .filter(z => z.roomUnit === ru.id && guestCheck(z.accommodation.guests, x.guests))
                // _ = log.info(s"${card.name}. 3. room variant exist.")
                pu <- card.priceUnits.filter(_.variant === rv.id)
                // _ = log.info(s"${card.name}. 4. price unit exist.")
                brd <- card.boardings.filter(_.id === pu.boarding)
                // _ = log.info(s"${card.name}. 5. property boarding exist.")
                pr = prices.filter(z => pu.id === z.priceUnit && z.group === cg.groupId)
                // _ = log.info(s"${card.name}. 6. prices: ${pr.length}.")
                pd = card.offerDates.filter(z => pr.exists(_.offerDate === z.id))
                // _ = log.info(s"${card.name}. 7. price dates: ${pd.length}.")
                ofr = card.offers.filter(z => pd.exists(_.offer === z.id))
                // _ = log.info(s"${card.name}. 8. offers: ${ofr.length}.")
                rls = card.offerRules.filter(z => ofr.exists(_.id === z.offer))
                // _ = log.info(s"${card.name}. 8. offers: ${ofr.length}.")
                trf <- card.tariffs.filter(z => pr.exists(_.tariff === z.id))
                // _ = log.info(s"${card.name}. 9. tariffs: ${ofr.length}.")
                ss = stopSales.filter(_.target.units.exists(_ === ru.id))
                // _ = log.info(s"${card.name}. 10. stop sales: ${ss.length}.")
                if pr.nonEmpty
              } yield roomVariant(
                lang = lang,
                currency = currency,
                checkInDates = checkInDates,
                guests = x.guests,
                propertyId = card.id,
                customerId = customerId,
                customerOrg = cg,
                room = x,
                tariff = trf,
                offers = ofr,
                offerRules = rls,
                priceDates = pd,
                roomUnit = ru,
                boarding = brd,
                roomVariant = rv,
                priceUnit = pu,
                prices = pr,
                stopSales = ss
              )).flatten
            )
          }
          .map(_.flatten.toList)
      }
    }
}

object LiveCalculatePropertyPricesAlg {
  final def apply(enableTrace: Boolean): CalculatePropertyPricesAlg =
    new LiveCalculatePropertyPricesAlg(enableTrace)
}
