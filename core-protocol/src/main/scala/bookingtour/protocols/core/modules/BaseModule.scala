package bookingtour.protocols.core.modules

import java.io.File
import java.time.Instant

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import tofu.logging.Logging
import zio.{UIO, ZIO}
import tofu.logging.zlogs.ZLogs

/**
  * © Alexey Toroshchin 2019.
  */
final class BaseModule private (
    val appConfig: Config,
    val isProduction: Boolean,
    val nodeId: Int,
    val enableTrace: Boolean
)(implicit val log: Logger, implicit val zioLog: UIO[Logging[ZIO[Any, Nothing, *]]]) {
  val systemStart: Instant             = Instant.now().minusSeconds(60L)
  def configPart(path: String): Config = appConfig.getConfig(path)
}

object BaseModule {
  final def apply(fileName: String, service: String): BaseModule = {
    val config = ConfigFactory
      .parseFile(new File(fileName))
      .resolve()
    val p = config.getBoolean("production")
    val n = config.getInt("node_id")
    val t = config.getBoolean(s"ms.$service.enable-trace")
    new BaseModule(appConfig = config, isProduction = p, nodeId = n, enableTrace = t)(
      log = Logger(s"bookingtour.$service"),
      zioLog = ZLogs.uio.byName(s"bookingtour.$service")
    )
  }
}
