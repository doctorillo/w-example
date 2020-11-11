package bookingtour.core.doobie

import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import zio._
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2019.
  */
trait DoobieZioConfig {

  _: BaseConfig =>

  val dbConfigKey: String
  val apiConfigKey: String

  final def dbName(dbTag: String): String = appConfig.getString(s"$dbConfigKey.$dbTag.databaseName")

  final def transactor(part: String, isPg: Boolean): Aux[Task, Unit] = {
    if (isPg) {
      Transactor.fromDriverManager[Task](
        "org.postgresql.Driver",
        s"jdbc:postgresql://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:5432/${appConfig
          .getString(s"$dbConfigKey.$part.databaseName")}?loginTimeout=300",
        appConfig.getString(s"$dbConfigKey.$part.user"),
        appConfig.getString(s"$dbConfigKey.$part.password")
      )
    } else {
      Transactor.fromDriverManager[Task](
        "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        s"jdbc:sqlserver://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:1433;databaseName=${appConfig
          .getString(s"$dbConfigKey.$part.databaseName")}",
        appConfig.getString(s"$dbConfigKey.$part.user"),
        appConfig.getString(s"$dbConfigKey.$part.password")
      )
    }
  }

  /*def managed(
    part: String,
    isPg: Boolean,
    ec: ExecutionContext,
    tc: ExecutionContext
  ): Managed[Throwable, HikariTransactor[Task]] = {
    val dbParam = if (isPg) {
      DbParamConfig(
        driverClassName = "org.postgresql.Driver",
        url =
          s"jdbc:postgresql://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:5432/${appConfig
            .getString(s"$dbConfigKey.$part.databaseName")}?loggerLevel=DEBUG&loginTimeout=300",
        user = appConfig.getString(s"$dbConfigKey.$part.user"),
        pass = appConfig.getString(s"$dbConfigKey.$part.password")
      )
    } else {
      DbParamConfig(
        driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        url =
          s"jdbc:sqlserver://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:1433;databaseName=${appConfig
            .getString(s"$dbConfigKey.$part.databaseName")}",
        user = appConfig.getString(s"$dbConfigKey.$part.user"),
        pass = appConfig.getString(s"$dbConfigKey.$part.password")
      )
    }
    val xa: Resource[Task, HikariTransactor[Task]] =
      HikariTransactor.newHikariTransactor(
        driverClassName = dbParam.driverClassName,
        url = dbParam.url,
        user = dbParam.user,
        pass = dbParam.pass,
        connectEC = ec,
        transactEC = tc
      )
    val res = xa.allocated.map {
      case (transactor, cleanupM) =>
        Reservation(ZIO.succeed(transactor), cleanupM.orDie)
    }
    Managed(res.uninterruptible)
  }*/
}
