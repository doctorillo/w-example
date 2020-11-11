package bookingtour.protocols.core.modules

import cats.data.NonEmptyList
import com.typesafe.config.Config
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2020.
  */
final class AeronModule private (services: NonEmptyList[AeronModule.Meta]) {
  def channel(serviceName: ServiceName): Option[String] = for {
    AeronModule
      .Meta(_, protocol, endpoint, group, control, ttl, timeout, _, _) <- services.find(
                                                                           _.name === serviceName
                                                                         )
  } yield s"aeron:$protocol?endpoint=$endpoint" //|ttl=$ttl,fc=$control,g:$group,t:$timeout"
}

object AeronModule {
  final case class Meta(
      name: ServiceName,
      protocol: String,
      endpoint: String,
      group: Int,
      control: String,
      ttl: String,
      timeout: String,
      inletStreamId: Int,
      outletStreamId: Int
  )

  private def fromConfig(ac: Config, name: ServiceName): AeronModule.Meta = {
    val c = ac.getConfig(name.value)
    Meta(
      name = name,
      group = c.getInt("group"),
      protocol = c.getString("protocol"),
      endpoint = c.getString("endpoint"),
      control = c.getString("control"),
      ttl = c.getString("ttl"),
      timeout = c.getString("timeout"),
      inletStreamId = s"${c.getInt("group")}1".toInt,
      outletStreamId = s"${c.getInt("group")}2".toInt,
    )
  }

  final def apply()(implicit bm: BaseModule): AeronModule = {
    val c                 = bm.appConfig.getConfig("aeron")
    val parties           = fromConfig(c, ServiceName.Parties)
    val properties        = fromConfig(c, ServiceName.Properties)
    val propertyPrices    = fromConfig(c, ServiceName.PropertyPrices)
    val orders            = fromConfig(c, ServiceName.Orders)
    val excursions        = fromConfig(c, ServiceName.Excursions)
    val interLook         = fromConfig(c, ServiceName.InterLook)
    val operationProvider = fromConfig(c, ServiceName.OperationProvider)
    val operationCustomer = fromConfig(c, ServiceName.OperationCustomer)
    val businessRules     = fromConfig(c, ServiceName.BusinessRules)
    val apiCustomer       = fromConfig(c, ServiceName.ApiCustomer)

    new AeronModule(
      NonEmptyList.fromListUnsafe(
        List(
          parties,
          properties,
          propertyPrices,
          orders,
          excursions,
          interLook,
          operationProvider,
          operationCustomer,
          businessRules,
          apiCustomer
        )
      )
    )
  }
}
