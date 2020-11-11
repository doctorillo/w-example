package bookingtour.core.doobie.modules

import java.util.concurrent.TimeUnit

import DataModule.TX
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import cats.implicits._
import doobie.util.transactor.Transactor
import zio.duration.Duration
import zio.interop.catz._
import zio.{Exit, Task, ZIO, blocking}

/**
  * © Alexey Toroshchin 2019.
  */
final class DataModule private (val dbName: String, val dbParN: Int, val dbBatchSize: Int)(
    implicit zioRuntime: zio.Runtime[zio.ZEnv],
    val xa: TX,
    duration: Duration
) {

  def run[F[_], A](query: TX => Task[F[A]]): Task[F[A]] = query(xa)

  def transact[F[_], A](query: TX => Task[F[A]])(
      cb: Either[List[Throwable], F[A]] => Unit
  ): Unit = {
    val effect: ZIO[zio.ZEnv, Throwable, Option[F[A]]] = query(xa)
      .catchAll(thr => ZIO.fail(new Exception(s"query error: ${thr.getMessage}.")))
      .timeout(duration)
    zioRuntime.unsafeRunSync(blocking.blocking(effect)) match {
      case zio.Exit.Failure(cause) =>
        val errors = cause.failures
        cb(errors.asLeft)

      case zio.Exit.Success(None) =>
        cb(List(new Exception("query time-out.")).asLeft)

      case Exit.Success(Some(value)) =>
        cb(value.asRight)
    }
  }

  def transactParallel(parN: Int)(queries: TX => Iterable[Task[Unit]])(
      cb: Either[List[Throwable], Unit] => Unit
  ): Unit = {
    val effects = ZIO
      .collectAllParN(parN)(queries(xa))
      .catchAll(thr => ZIO.fail(new Exception(s"query error: ${thr.getMessage}.")))
      .unit
      .timeout(duration)
    zioRuntime.unsafeRunSync(blocking.blocking(effects)) match {
      case zio.Exit.Failure(cause) =>
        val errors = cause.failures
        cb(errors.asLeft)

      case zio.Exit.Success(None) =>
        cb(List(new Exception("query time-out.")).asLeft)

      case Exit.Success(Some(value)) =>
        cb(value.asRight)
    }
  }

}

object DataModule {
  final type TX = Transactor.Aux[Task, Unit]

  final def apply(
      service: String,
      timeoutMs: Long
  )(implicit baseModule: BaseModule, runtimeModule: RuntimeModule): DataModule = {
    import baseModule._
    import runtimeModule._
    val dbConfig    = appConfig.getConfig(s"db.$service")
    val dbName      = dbConfig.getString("db-name")
    val dataSource  = dbConfig.getString("data-source")
    val url         = dbConfig.getString("url")
    val user        = dbConfig.getString("user")
    val password    = dbConfig.getString("password")
    val dbParN      = appConfig.getInt(s"operation.dbParN")
    val dbBatchSize = appConfig.getInt(s"operation.dbBatch")
    implicit val xa: TX =
      Transactor.fromDriverManager(driver = dataSource, url = url, user = user, pass = password)
    implicit val duration: Duration = Duration(timeoutMs, TimeUnit.MILLISECONDS)
    new DataModule(dbName, dbParN = dbParN, dbBatchSize = dbBatchSize)
  }

}
