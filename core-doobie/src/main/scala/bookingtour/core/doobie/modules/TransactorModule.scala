package bookingtour.core.doobie.modules

import cats.effect.Blocker
import com.typesafe.config.Config
import doobie.hikari.HikariTransactor
import zio.interop.catz._
import zio.{Managed, Reservation, Task, ZIO}

import scala.concurrent.ExecutionContext

/**
  * © Alexey Toroshchin 2020.
  */
object TransactorModule {

  final def make(
      config: Config,
      service: String,
      connectionEC: ExecutionContext,
      blockingEC: ExecutionContext,
      timeoutMs: Long
  ): Managed[Throwable, HikariTransactor[Task]] = {
    val dbConfig = config.getConfig(s"db.$service")
    val driver   = dbConfig.getString("driver")
    val url      = dbConfig.getString("url")
    val user     = dbConfig.getString("user")
    val password = dbConfig.getString("password")
    val xa = HikariTransactor.newHikariTransactor[Task](
      driverClassName = driver,
      url = url,
      pass = password,
      user = user,
      connectEC = connectionEC,
      blocker = Blocker.liftExecutionContext(blockingEC)
    )
    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), _ => cleanupM.orDie)
    }.uninterruptible
    Managed(res)
  }

}
