package bookingtour.protocols.business.rules.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.business.rules.env.{CalculatePropertyPricesAlg, RawPropertyPriceDataAlg}
import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Month, Year}
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.Ranges.Dates._
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem}
import bookingtour.protocols.parties.api.queries.QueryGroup
import bookingtour.protocols.parties.env.RelationCustomerAlg
import bookingtour.protocols.property.prices.agg.StopSaleVKey
import bookingtour.protocols.property.prices.api.PriceOp.PricePartition
import bookingtour.protocols.property.prices.api._
import bookingtour.protocols.property.prices.env.RoomVariantAlg
import cats.instances.all._
import cats.syntax.order._
import com.typesafe.scalalogging.Logger
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveRawPropertyPriceDataAlg private (
    implicit relationAlg: RelationCustomerAlg.Service[Any],
    roomVariantAlg: RoomVariantAlg.Service[Any],
    serviceCalculatePropertyPrices: CalculatePropertyPricesAlg.Service[Any],
    priceConsumerAlg: ConsumerAlg.Aux[Any, PricePartition, PriceOp],
    stopSaleConsumerAlg: ConsumerAlg.Aux[Any, StopSaleVKey, StopSaleVILP]
) extends RawPropertyPriceDataAlg {

  private val log = Logger(getClass)

  val rawPropertyPriceDataAlg: RawPropertyPriceDataAlg.Service[Any] =
    (
        lang: LangItem,
        query: QueryGroup,
        dates: Ranges.Dates,
        cards: PropertyCardProductContainer
    ) => {
      val tag: String = "raw-property-price-data-alg"
      (for {
        startA         <- ZIO.effectTotal(System.currentTimeMillis())
        groups         = cards.customer.askTerminate()
        _              <- ZIO.effectTotal(log.info(s"$tag. groups: ${groups.size}."))
        _              <- ZIO.fail(s"$tag. groups is empty.").when(groups.isEmpty)
        queryBoardings = query.rooms.flatMap(_.guests.flatMap(_.boarding.toList))
        _              <- ZIO.effectTotal(log.info(s"$tag. query-boardings: ${queryBoardings.mkString(",")}."))
        months         = List(Month(dates.from.getMonthValue), Month(dates.to.getMonthValue)).distinct
        _              <- ZIO.effectTotal(log.info(s"$tag. months: ${months.mkString(",")}."))
        years          = List(Year(dates.from.getYear), Year(dates.to.getYear)).distinct
        _              <- ZIO.effectTotal(log.info(s"$tag. years: ${years.mkString(",")}."))
        roomVariants   <- roomVariantAlg.fetch(query, cards.properties).map(_.filter(_._3.nonEmpty))
        offerDates     = roomVariants.flatMap(x => x._2.offerDates.filter(_.dates.intersected(dates)))
        _              <- ZIO.fail(s"$tag. offer dates is empty.").when(offerDates.isEmpty)
        variants       = roomVariants.flatMap(_._3)
        _              <- ZIO.fail(s"$tag. variants of room is empty.").when(variants.isEmpty)
        properties     = variants.map(_.property).distinct
        _              <- ZIO.effectTotal(log.info(s"$tag. properties: ${properties.length}."))
        providers      = roomVariants.map(_._2.supplier).distinct
        _              <- ZIO.effectTotal(log.info(s"$tag. providers: ${providers.length}."))
        suppliers      = roomVariants.map(_._2.supplier).distinct
        _              <- ZIO.effectTotal(log.info(s"$tag. suppliers: ${suppliers.length}."))
        startB         <- ZIO.effectTotal(System.currentTimeMillis())
        priceFork <- priceConsumerAlg
                      .byKey(x => groups.exists(_.groupId === x.group) && variants.exists(_.id === x.variant))
                      .fork
        stopSaleFork <- stopSaleConsumerAlg
                         .byKey(x =>
                           properties.contains(x.propertyId) && months.contains(x.month) && years.contains(
                             x.year
                           )
                         )
                         .fork
        prices    <- priceFork.join
        startC    <- ZIO.effectTotal(System.currentTimeMillis())
        _         <- ZIO.fail(s"$tag. prices is empty.").when(prices.isEmpty)
        stopSales <- stopSaleFork.join
        startD    <- ZIO.effectTotal(System.currentTimeMillis())
        l <- ZIO.collectAllSuccessesParN(8)(
              cards.properties.map(x =>
                serviceCalculatePropertyPrices
                  .run(
                    lang = lang,
                    currency = CurrencyItem.Euro,
                    dates = dates,
                    groups = groups,
                    customer = cards.customer.partyId,
                    rooms = query.rooms,
                    card = x,
                    prices = prices,
                    stopSales = stopSales
                  )
                  .map(z => (x, z))
              )
            )
        finish <- ZIO.effectTotal(System.currentTimeMillis())
        _ <- ZIO.effectTotal(
              log.info(s"$tag. 1. base filtering. ${variants.length}. ${startB - startA} ms.")
            )
        _ <- ZIO.effectTotal(
              log.info(s"$tag. 2. prices. ${prices.length}. stop-sales. ${stopSales.length}. ${startC - startB} ms.")
            )
        _ <- ZIO.effectTotal(log.info(s"$tag. 3. price variants. ${l.length}. ${finish - startD} ms."))
        _ <- ZIO.effectTotal(log.info(s"$tag. total. ${finish - startA} ms."))
      } yield l.toMap).catchAll { err =>
        log.error(err)
        ZIO.succeed(Map.empty[PropertyPriceCardProduct, List[PriceVariantUI]])
      }
    }
}

object LiveRawPropertyPriceDataAlg {
  final def apply()(
      implicit relationAlg: RelationCustomerAlg.Service[Any],
      roomVariantAlg: RoomVariantAlg.Service[Any],
      serviceCalculatePropertyPrices: CalculatePropertyPricesAlg.Service[Any],
      priceConsumerAlg: ConsumerAlg.Aux[Any, PricePartition, PriceOp],
      stopSaleConsumerAlg: ConsumerAlg.Aux[Any, StopSaleVKey, StopSaleVILP]
  ): RawPropertyPriceDataAlg = new LiveRawPropertyPriceDataAlg()
}
