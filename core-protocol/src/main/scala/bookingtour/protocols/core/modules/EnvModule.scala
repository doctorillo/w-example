package bookingtour.protocols.core.modules

import java.util.UUID
import scala.jdk.CollectionConverters._

/**
  * © Alexey Toroshchin 2020.
  */
final class EnvModule private (val rootCountries: List[UUID], val rootProviders: List[UUID])

object EnvModule {
  final def apply()(implicit bm: BaseModule): EnvModule = {
    val c = bm.appConfig
      .getStringList("operation.rootCountries")
      .asScala
      .toList
      .map(UUID.fromString)
    val p = bm.appConfig
      .getStringList("operation.rootProviders")
      .asScala
      .toList
      .map(UUID.fromString)
    new EnvModule(c, p)
  }
}
