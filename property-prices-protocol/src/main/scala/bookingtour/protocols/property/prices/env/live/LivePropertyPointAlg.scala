package bookingtour.protocols.property.prices.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.enumeration.{LangItem, PointItem}
import bookingtour.protocols.parties.agg.basic.{CityAgg, CountryAgg, RegionAgg}
import bookingtour.protocols.parties.api.PointUI
import bookingtour.protocols.parties.newTypes.{CityId, CountryId, PointId}
import bookingtour.protocols.property.prices.env.PropertyPointAlg
import cats.syntax.option._
import cats.syntax.order._
import zio.{URIO, ZIO}
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.property.prices.api.PropertyPriceCardProduct

/**
  * © Alexey Toroshchin 2019.
  */
final class LivePropertyPointAlg private (
    rootCountries: List[CountryId],
    countryAlg: ConsumerAlg.Aux[Any, Int, CountryAgg],
    regionAlg: ConsumerAlg.Aux[Any, Int, RegionAgg],
    cityAlg: ConsumerAlg.Aux[Any, Int, CityAgg],
    cardAlg: ConsumerAlg.Aux[Any, CityId, PropertyPriceCardProduct]
) extends PropertyPointAlg {
  val propertyPointAlg: PropertyPointAlg.Service[Any] = new PropertyPointAlg.Service[Any] {
    def fetchAll(lang: LangItem): URIO[Any, List[PointUI]] =
      for {
        a        <- cardAlg.byValue(x => rootCountries.contains(x.country)).catchAll(_ => ZIO.succeed(List.empty))
        pcountry <- ZIO.effectTotal(a.map(_.country).distinct)
        pregion  <- ZIO.effectTotal(a.map(_.region).distinct)
        pcity    <- ZIO.effectTotal(a.map(_.city).distinct)
        cnt <- countryAlg
                .byValue(x => pcountry.contains(x.id))
                .catchAll(_ => ZIO.succeed(List.empty))
        rgn <- regionAlg
                .byValue(x => pregion.contains(x.id))
                .catchAll(_ => ZIO.succeed(List.empty))
        ct <- cityAlg
               .byValue(x => pcity.contains(x.id))
               .catchAll(_ => ZIO.succeed(List.empty))
        countries = for {
          it <- cnt
          lbl <- it.labels
                  .filter(_.lang === lang)
                  .map(_.toApi)
        } yield PointUI(PointId(it.id.x), None, lbl, PointItem.Country)
        regions = for {
          it <- rgn
          lbl <- it.labels
                  .filter(_.lang === lang)
                  .map(_.toApi)
        } yield PointUI(
          id = PointId(it.id.x),
          parent = PointId(it.country.id.x).some,
          label = lbl,
          category = PointItem.Region
        )
        cities = for {
          it <- ct
          lbl <- it.labels
                  .filter(_.lang === lang)
                  .map(_.toApi)
        } yield PointUI(
          id = PointId(it.id.x),
          parent = PointId(it.region.id.x).some,
          label = lbl,
          category = PointItem.City
        )
      } yield (countries ++ regions ++ cities).distinct

    def fetchCities(
        pointId: PointId,
        pointType: PointItem
    ): URIO[Any, List[CityId]] = pointType match {
      case PointItem.Country =>
        for {
          a <- fetchAll(LangItem.Ru)
          b <- ZIO.effectTotal(a.filter(_.id === pointId))
          c <- ZIO.effectTotal(a.filter(_.parent.exists(x => b.exists(_.id === x))))
          d <- ZIO.effectTotal(a.filter(_.parent.exists(x => c.exists(_.id === x))))
          e = d.map(x => CityId(x.id.x))
        } yield e

      case PointItem.Region =>
        for {
          a <- fetchAll(LangItem.Ru)
          b <- ZIO.effectTotal(a.filter(_.id === pointId))
          d <- ZIO.effectTotal(a.filter(_.parent.exists(x => b.exists(_.id === x))))
          e = d.map(x => CityId(x.id.x))
        } yield e

      case PointItem.City =>
        for {
          a <- fetchAll(LangItem.Ru)
          b <- ZIO.effectTotal(a.filter(_.id === pointId))
          e = b.map(x => CityId(x.id.x))
        } yield e

      case _ =>
        ZIO.succeed(List.empty)
    }
  }
}

object LivePropertyPointAlg {
  final def apply(rootCountries: List[CountryId])(
      implicit countryAlg: ConsumerAlg.Aux[Any, Int, CountryAgg],
      regionAlg: ConsumerAlg.Aux[Any, Int, RegionAgg],
      cityAlg: ConsumerAlg.Aux[Any, Int, CityAgg],
      cardAlg: ConsumerAlg.Aux[Any, CityId, PropertyPriceCardProduct]
  ): PropertyPointAlg =
    new LivePropertyPointAlg(
      rootCountries = rootCountries,
      countryAlg = countryAlg,
      regionAlg = regionAlg,
      cityAlg = cityAlg,
      cardAlg = cardAlg
    )
}
