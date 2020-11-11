package bookingtour.core.doobie

import java.util.concurrent.TimeUnit

import cats.effect.{ContextShift, IO}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

/**
  * © Alexey Toroshchin 2019.
  */
trait DoobieConfig {
  _: BaseConfig =>

  val dbConfigKey: String
  val apiConfigKey: String

  final def dbName(dbTag: String): String = appConfig.getString(s"$dbConfigKey.$dbTag.databaseName")

  final def transactor(part: String, isPg: Boolean)(
      implicit cs: ContextShift[IO]
  ): Aux[IO, Unit] = {
    if (isPg) {
      Transactor.fromDriverManager(
        "org.postgresql.Driver",
        s"jdbc:postgresql://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:5432/${appConfig
          .getString(s"$dbConfigKey.$part.databaseName")}?loggerLevel=DEBUG&loginTimeout=300",
        appConfig.getString(s"$dbConfigKey.$part.user"),
        appConfig.getString(s"$dbConfigKey.$part.password")
      )
    } else {
      Transactor.fromDriverManager(
        "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        s"jdbc:sqlserver://${appConfig.getString(s"$dbConfigKey.$part.serverName")}:1433;databaseName=${appConfig
          .getString(s"$dbConfigKey.$part.databaseName")}",
        appConfig.getString(s"$dbConfigKey.$part.user"),
        appConfig.getString(s"$dbConfigKey.$part.password")
      )
    }
  }

  final def dataSource(part: String, poolSize: Int): HikariDataSource = {
    val dsConf = new HikariConfig()
    dsConf.setDataSourceClassName(appConfig.getString(s"$dbConfigKey.$part.dataSource"))
    dsConf.addDataSourceProperty(
      "serverName",
      appConfig.getString(s"$dbConfigKey.$part.serverName")
    )
    dsConf.addDataSourceProperty(
      "databaseName",
      appConfig.getString(s"$dbConfigKey.$part.databaseName")
    )
    dsConf.addDataSourceProperty("user", appConfig.getString(s"$dbConfigKey.$part.user"))
    dsConf.addDataSourceProperty("password", appConfig.getString(s"$dbConfigKey.$part.password"))
    dsConf.setPoolName(s"$part-hikari")
    dsConf.setMaximumPoolSize(poolSize)
    dsConf.setValidationTimeout(TimeUnit.MINUTES.toMillis(1))
    dsConf.setMinimumIdle(0)
    dsConf.setMaxLifetime(TimeUnit.MINUTES.toMillis(2))
    dsConf.setIdleTimeout(TimeUnit.MINUTES.toMillis(1))
    dsConf.setConnectionTimeout(TimeUnit.MINUTES.toMillis(5))
    dsConf.setInitializationFailTimeout(1000L)
    new HikariDataSource(dsConf)
  }
}
