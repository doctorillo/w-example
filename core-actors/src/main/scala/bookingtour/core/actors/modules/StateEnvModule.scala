package bookingtour.core.actors.modules

import bookingtour.protocols.core.modules.BaseModule
import com.typesafe.config.Config

/**
  * © Alexey Toroshchin 2019.
  */
final class StateEnvModule private (
    val persistIdx: Int,
    val persistEnabled: Boolean,
    val persistClean: Boolean,
    val timeoutSeconds: Long,
    val ttlSeconds: Long,
    val heartBeatWaitSeconds: Long
)

object StateEnvModule {
  final def apply(service: String)(implicit bm: BaseModule): StateEnvModule = {
    val sc: Config = bm.appConfig.getConfig(s"ms.$service")
    val idx        = sc.getInt("state-index")
    val e          = sc.getBoolean("state-enable")
    val c          = sc.getBoolean("state-clean")
    val t          = sc.getLong("state-timeout")
    val ttl        = sc.getLong("state-ttl-seconds")
    val hb         = sc.getLong("akka.kafka.heart-beat-seconds")
    new StateEnvModule(
      persistIdx = idx,
      persistEnabled = e,
      persistClean = c,
      timeoutSeconds = t,
      ttlSeconds = ttl,
      heartBeatWaitSeconds = Math.round(hb + 7)
    )
  }
}
