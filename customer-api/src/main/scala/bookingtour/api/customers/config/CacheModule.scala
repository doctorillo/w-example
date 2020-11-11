package bookingtour.api.customers.config

import bookingtour.core.actors.cache.AlgCacheProvider
import bookingtour.core.actors.modules.StateEnvModule
import bookingtour.core.finch.environment.impl.LiveAlgCacheProvider
import bookingtour.protocols.core.cache.CacheStoreKey
import bookingtour.protocols.core.modules.BaseModule
import cats.syntax.option._
import com.twitter.finagle.Redis
import com.twitter.finagle.redis.Client

/**
  * © Alexey Toroshchin 2019.
  */
final class CacheModule private (implicit bm: BaseModule, sem: StateEnvModule) {
  import bm._
  import sem._

  private val redis: Client = {
    val path = if (isProduction) {
      appConfig.getString("redis.bootstrap")
    } else {
      appConfig.getString("redis.bootstrapDev")
    }
    Redis.newRichClient(path)
  }

  val cacheProvider: Option[AlgCacheProvider] = if (persistEnabled) {
    LiveAlgCacheProvider
      .make(
        redisClient = redis,
        storeKey = CacheStoreKey(persistIdx),
        timeoutMs = timeoutSeconds,
        entityTTLSeconds = ttlSeconds
      )
      .some
  } else {
    none
  }
  val cacheSessionProvider: AlgCacheProvider = cacheProvider.getOrElse(
    LiveAlgCacheProvider
      .make(
        redisClient = redis,
        storeKey = CacheStoreKey(persistIdx),
        timeoutMs = timeoutSeconds,
        entityTTLSeconds = ttlSeconds
      )
  )
}

object CacheModule {
  final def apply()(implicit bm: BaseModule, sem: StateEnvModule): CacheModule =
    new CacheModule
}
