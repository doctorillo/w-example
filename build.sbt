autoCompilerPlugins := true

// updateOptions := updateOptions.value.withCachedResolution(true)

// dependencyUpdatesFilter -= moduleFilter(organization = "org.scala-lang")

cancelable := true

fork := true

maxErrors := 1

logBuffered in Test := false

addCommandAlias(
  "runApp",
  "; project /; compile; project watchMs; reStart; project interLookMs; reStart; project partiesMs; reStart; project propertiesMs; reStart;"
)

addCommandAlias("stopApp", "; project /; reStop;")

addCommandAlias("runParty", "project partiesMs; reStart")

addCommandAlias("stopParty", "; project partiesMs; reStop")

val DebugTest = config("dtest").extend(Test)

val jvmOptsProduction = Seq(
  "-Djava.net.preferIPv4Stack=true",
  "-Dsun.reflect.inflationThreshold=2147483647",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:ReservedCodeCacheSize=128m",
  "-Xss8M",
  "-XX:SurvivorRatio=128",
  "-XX:MaxTenuringThreshold=0",
  "-server"
)

val localIvy =
  Resolver.file("localIvy", file(Path.userHome.absolutePath + "/.ivy2"))(
    Resolver.ivyStylePatterns
  )
val localMav =
  "Local Maven Repository".at("file://" + Path.userHome.absolutePath + "/.m2/repository")

val scalaMeta =
  Resolver.url("scalameta", url("https://dl.bintray.com/scalameta/maven"))(
    Resolver.ivyStylePatterns
  )
val typesafe = "Typesafe Releases".at("https://repo.typesafe.com/typesafe/maven-releases/")
val sona     = "Sonatype OSS Releases".at("https://oss.sonatype.org/content/repositories/releases")
val sonasnapshot =
  "Sonatype OSS Releases".at("https://oss.sonatype.org/content/repositories/snapshots")
val implicitErr   = Resolver.bintrayRepo("tek", "maven")
val cakesolutions = Resolver.bintrayRepo("cakesolutions", "maven")

val confluent = "confluent.io".at("https://packages.confluent.io/maven/")

val resolverOpt = Seq(
  localIvy,
  localMav,
  Resolver.jcenterRepo,
  Resolver.bintrayRepo("cakesolutions", "maven"),
  Resolver.bintrayRepo("scalameta", "maven"),
  "confluent.io".at("https://packages.confluent.io/maven/"),
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  implicitErr
)
val scalacOpt = Seq(
  "-deprecation",
  "-encoding",
  "utf8",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:existentials",
  "-unchecked",
  "-feature",
  "-explaintypes",
  //"-Ypartial-unification",
  "-Ywarn-value-discard",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-Ywarn-unused:implicits",
  "-Ywarn-extra-implicit",
  "-Ywarn-numeric-widen",
  //"-Ywarn-adapted-args",
  "-Ymacro-annotations",
  "-Yrangepos",
  "-Xlog-free-terms",
  "-Xlog-free-types",
  "-Xlog-reflective-calls",
  "-Ycache-plugin-class-loader:last-modified",
  "-Ycache-macro-class-loader:last-modified",
  "-Ybackend-parallelism",
  "8"
)

lazy val kafkaSettings = Seq(
  libraryDependencies ++= Seq(
    ("org.apache.kafka" % "kafka-clients" % "2.5.0")
      .exclude("org.slf4j", "slf4j-log4j12")
      .exclude("log4j", "log4j")
  )
)

lazy val aeronSettings = Seq(
  libraryDependencies ++= Seq(
    "io.aeron" % "aeron-all" % "1.27.0"
  )
)

val akkaVersion = "2.6.4"
lazy val akkaSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor"  % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    ("com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2").excludeAll(
      ExclusionRule(
        organization = "org.apache.kafka"
      )
    ),
    ("com.typesafe.akka" %% "akka-slf4j" % akkaVersion).excludeAll(
      ExclusionRule(
        organization = "ch.qos.logback"
      ),
      ExclusionRule(organization = "org.slf4j")
    )
  )
)

val twitterServerVersion = "20.4.0"
val finchVersion         = "0.32.1"
val zioTwitterVersion    = "20.4.0.0-RC1"
lazy val finagleSettings = Seq(
  libraryDependencies ++= Seq(
    "com.twitter"        %% "twitter-server" % twitterServerVersion,
    "com.github.finagle" %% "finchx-core"    % finchVersion,
    ("com.github.finagle" %% "finchx-circe" % finchVersion)
      .excludeAll(ExclusionRule(organization = "io.circe")),
    "dev.zio"            %% "zio-interop-twitter" % zioTwitterVersion,
    "com.github.finagle" %% "finchx-test"         % finchVersion % Test
  )
)

lazy val redisSettings = Seq(
  libraryDependencies ++= Seq(
    "com.twitter" %% "finagle-redis"       % twitterServerVersion,
    "dev.zio"     %% "zio-interop-twitter" % zioTwitterVersion
  )
)

val postgresqlVersion = "42.2.12"
lazy val postgresqlSettings = Seq(
  libraryDependencies ++= Seq(
    "org.postgresql" % "postgresql" % postgresqlVersion
  )
)

val mssqlVersion = "8.3.0.jre14-preview"
lazy val mssqlSettings = Seq(
  libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % mssqlVersion
)

val mysqlVersion = "8.0.16"
lazy val mysqlSettings = Seq(
  libraryDependencies += "mysql" % "mysql-connector-java" % mysqlVersion
)

val doobieVersion = "0.9.0"
lazy val doobieSettings = Seq(
  libraryDependencies ++= Seq(
    "org.tpolecat" %% "doobie-core"      % doobieVersion,
    "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
    "org.tpolecat" %% "doobie-hikari"    % doobieVersion,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test
  )
)

val circeVersion = "0.13.0"
lazy val circeJsonSettings = Seq(
  libraryDependencies ++= Seq(
    "circe-core",
    "circe-generic",
    "circe-parser",
    "circe-literal"
  ).map("io.circe" %% _                  % circeVersion) ++ Seq(
    "io.circe"     %% "circe-derivation" % "0.13.0-M4",
    ("com.beachape" %% "enumeratum-circe" % "1.5.23").excludeAll(
      ExclusionRule(
        organization = "io.circe"
      )
    )
  )
)

val algebraVersion    = "2.0.1"
val shapelessVersion  = "2.3.3"
val catsVersion       = "2.1.1"
val catsEffectVersion = "2.1.2"
val enumeratumVersion = "1.5.15"
val derevoVersion     = "0.11.2"
val tofuVersion       = "0.7.4"
val zioVersion        = "1.0.0-RC18-2"
val zioMacros         = "0.4.0"
val fmtConfig         = file(".scalafmt.conf")
lazy val commonSettings = Seq(
  organization := "eu.d0c.wellness",
  autoScalaLibrary := true,
  logBuffered in Test := false,
  scalaVersion := "2.13.1",
  javacOptions ++= Seq("-source", "14"),
  resolvers ++= resolverOpt,
  scapegoatVersion in ThisBuild := "1.3.11",
  managedClasspath in Compile := {
    val res = (resourceDirectory in Compile).value
    val old = (managedClasspath in Compile).value
    Attributed.blank(res) +: old
  },
  addCompilerPlugin("org.typelevel" % "kind-projector_2.13.1" % "0.11.0"),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for"   % "0.3.1"),
  addCompilerPlugin(scalafixSemanticdb),
  //addCompilerPlugin("com.softwaremill.neme" %% "neme-plugin"           % "0.0.5"),
  //scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.3.2",
  scalacOptions ++= scalacOpt,
  scalafmtConfig := fmtConfig,
  dependencyUpdatesFilter -= moduleFilter(organization = "org.scala-lang"),
  libraryDependencies ++= Seq(
    "org.scala-lang"             % "scala-library"        % scalaVersion.value,
    "com.typesafe"               % "config"               % "1.4.0",
    "com.typesafe.scala-logging" %% "scala-logging"       % "3.9.2",
    "ch.qos.logback"             % "logback-classic"      % "1.2.3",
    "org.slf4j"                  % "slf4j-api"            % "1.7.30",
    "org.typelevel"              %% "algebra-laws"        % algebraVersion % Test,
    "org.typelevel"              %% "cats-core"           % catsVersion,
    "org.typelevel"              %% "cats-free"           % catsVersion,
    "org.typelevel"              %% "cats-laws"           % catsVersion % Test,
    "org.typelevel"              %% "cats-effect"         % catsEffectVersion,
    "com.beachape"               %% "enumeratum"          % enumeratumVersion,
    "io.estatico"                %% "newtype"             % "0.4.3",
    "eu.timepit"                 %% "refined"             % "0.9.13",
    "eu.timepit"                 %% "refined-cats"        % "0.9.13",
    "io.scalaland"               %% "chimney"             % "0.5.0",
    "dev.zio"                    %% "zio"                 % zioVersion,
    "dev.zio"                    %% "zio-streams"         % zioVersion,
    "dev.zio"                    %% "zio-interop-cats"    % "2.0.0.0-RC12",
    "dev.zio"                    %% "zio-macros-access"   % zioMacros,
    "dev.zio"                    %% "zio-macros-delegate" % zioMacros,
    "ru.tinkoff"                 %% "tofu-core"           % tofuVersion,
    //"ru.tinkoff"                 %% "tofu-env"                % tofuVersion,
    "ru.tinkoff"  %% "tofu-optics-core"        % tofuVersion,
    "ru.tinkoff"  %% "tofu-optics-macro"       % tofuVersion,
    "ru.tinkoff"  %% "tofu-data"               % tofuVersion,
    "ru.tinkoff"  %% "tofu-logging"            % tofuVersion,
    "ru.tinkoff"  %% "tofu-logging-derivation" % tofuVersion,
    "ru.tinkoff"  %% "tofu-logging-structured" % tofuVersion,
    "ru.tinkoff"  %% "tofu-enums"              % tofuVersion,
    "ru.tinkoff"  %% "tofu-zio-interop"        % tofuVersion,
    "ru.tinkoff"  %% "tofu-zio-logging"        % tofuVersion,
    "org.manatki" %% "derevo-core"             % derevoVersion,
    "org.manatki" %% "derevo-cats"             % derevoVersion,
    ("org.manatki" %% "derevo-circe" % derevoVersion)
      .excludeAll(ExclusionRule(organization = "io.circe")),
    "com.github.t3hnar" %% "scala-bcrypt"     % "4.1",
    "org.scalatest"     %% "scalatest"        % "3.1.1" % Test,
    "org.typelevel"     %% "cats-testkit"     % catsVersion % Test,
    "com.lihaoyi"       % "ammonite"          % "2.0.4" % Test cross CrossVersion.full,
    "org.scalameta"     %% "munit"            % "0.7.2" % Test,
    "org.scalameta"     %% "munit-scalacheck" % "0.7.2" % Test,
    "javax.validation"  % "validation-api"    % "2.0.1.Final"
  ),
  testFrameworks += new TestFramework("munit.Framework"),
  sourceGenerators in Test += Def.task {
    val file = (sourceManaged in Test).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,
  cancelable := true,
  fork := true,
  maxErrors := 1,
  crossPaths := false
)

lazy val coreProtocol = (project in file("core-protocol"))
  .settings(commonSettings: _*)
  .settings(akkaSettings: _*)
  .settings(circeJsonSettings: _*)
  .settings(
    name := "core-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val doobieProtocol = (project in file("doobie-protocol"))
  .dependsOn(coreProtocol)
  .settings(commonSettings: _*)
  .settings(doobieSettings: _*)
  .settings(
    name := "doobie-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val actorsProtocol = (project in file("actors-protocol"))
  .dependsOn(doobieProtocol)
  .settings(commonSettings: _*)
  .settings(akkaSettings: _*)
  .settings(
    name := "actors-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val interLookProtocol = (project in file("inter-look-protocol"))
  .dependsOn(doobieProtocol, coreActors)
  .settings(commonSettings: _*)
  .settings(
    name := "inter-look-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val partiesProtocol = (project in file("parties-protocol"))
  .dependsOn(doobieProtocol, coreActors, interLookProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "parties-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val propertiesProtocol = (project in file("properties-protocol"))
  .dependsOn(doobieProtocol, coreActors, partiesProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "properties-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val propertyPricesProtocol = (project in file("property-prices-protocol"))
  .dependsOn(doobieProtocol, coreActors, interLookProtocol, partiesProtocol, propertiesProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "property-prices-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val interLookOrdersProtocol = (project in file("inter-look-orders-protocol"))
  .dependsOn(doobieProtocol, coreActors)
  .settings(commonSettings: _*)
  .settings(
    name := "inter-look-orders-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val businessRuleProtocol = (project in file("business-rules-protocol"))
  .dependsOn(coreProtocol, partiesProtocol, propertiesProtocol, propertyPricesProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "business-rules-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val excursionsProtocol = (project in file("excursions-protocol"))
  .dependsOn(coreProtocol, partiesProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "excursions-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val ordersProtocol = (project in file("orders-protocol"))
  .dependsOn(doobieProtocol, partiesProtocol, propertiesProtocol, propertyPricesProtocol, excursionsProtocol)
  .settings(commonSettings: _*)
  .settings(
    name := "orders-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val apiProtocol = (project in file("api-protocol"))
  .dependsOn(
    coreProtocol,
    doobieProtocol,
    actorsProtocol,
    partiesProtocol,
    propertiesProtocol,
    propertyPricesProtocol,
    ordersProtocol,
    interLookProtocol,
    interLookOrdersProtocol,
    businessRuleProtocol,
    excursionsProtocol
  )
  .settings(commonSettings: _*)
  .settings(circeJsonSettings: _*)
  .settings(
    name := "api-protocol"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val coreDoobie = (project in file("core-doobie"))
  .dependsOn(doobieProtocol)
  .settings(commonSettings: _*)
  .settings(postgresqlSettings: _*)
  .settings(mssqlSettings: _*)
  .settings(
    name := "core-doobie"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val coreKafka = (project in file("core-kafka"))
  .dependsOn(coreProtocol)
  .settings(commonSettings: _*)
  .settings(kafkaSettings: _*)
  .settings(
    name := "core-kafka"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val coreActors = (project in file("core-actors"))
  .dependsOn(actorsProtocol, coreDoobie, coreKafka)
  .settings(commonSettings: _*)
  .settings(redisSettings: _*)
  .settings(
    name := "core-actors"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val coreFinch = (project in file("core-finch"))
  .dependsOn(coreProtocol, coreActors, partiesProtocol)
  .settings(commonSettings: _*)
  .settings(circeJsonSettings: _*)
  .settings(akkaSettings: _*)
  .settings(finagleSettings: _*)
  .settings(
    name := "core-finch"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val partiesData = (project in file("parties-data"))
  .dependsOn(coreDoobie, partiesProtocol)
  .settings(commonSettings: _*)
  .settings(doobieSettings: _*)
  .settings(
    name := "parties-data"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val partiesMs = (project in file("parties-ms"))
  .dependsOn(coreActors, partiesData, interLookProtocol)
  .settings(commonSettings: _*)
  .settings(
    fork in DebugTest := true,
    excludeDependencies += "org.slf4j" % "slf4j-log4j12",
    mappings in (Compile, packageBin) ~= {
      _.filterNot(x => x._1.getName.equals("deployment.properties") || x._1.getName.equals("logback.xml"))
    },
    definedTests in DebugTest := (definedTests in Test).value,
    reColors := Seq("blue", "green", "magenta"),
    mainClass in reStart := Some("bookingtour.ms.parties.Boot"),
    reStartArgs := Seq("config_path=/path_conf/wellness.conf"),
    Revolver.enableDebugging(port = 5556, suspend = false),
    version := "0.1",
    name := "parties-ms",
    packMain := Map("parties-ms" -> "bookingtour.ms.parties.Boot"),
    packJvmOpts := Map(
      "parties-ms" -> Seq(
        "-Dlogback.configurationFile=/home/d0c/conf/logback-parties-ms.xml",
        "-Dconfig_path=/path_conf/wellness.conf",
        "-Djava.net.preferIPv4Stack=true",
        "-Dsun.reflect.inflationThreshold=2147483647",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseShenandoahGC",
        "-XX:+AlwaysPreTouch",
        "-XX:+DisableExplicitGC",
        "-XX:+ClassUnloadingWithConcurrentMark",
        "-XX:+UseCompressedOops",
        "-XX:ReservedCodeCacheSize=256m",
        "-XX:MaxMetaspaceSize=1G",
        "-XX:MaxInlineLevel=18",
        "-Xss8M",
        "-Xms1G",
        "-Xmx2G",
        "-XX:SurvivorRatio=128",
        "-XX:MaxTenuringThreshold=0",
        "-server"
      )
    ),
    packGenerateWindowsBatFile := false,
    packJarNameConvention := "default",
    packGenerateMakefile := false
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val apiCustomerMs = (project in file("customer-api"))
  .configs(DebugTest)
  .dependsOn(
    coreActors,
    coreDoobie,
    coreFinch,
    interLookProtocol,
    partiesProtocol,
    propertiesProtocol,
    propertyPricesProtocol,
    interLookOrdersProtocol,
    ordersProtocol,
    apiProtocol
  )
  .settings(commonSettings: _*)
  .settings(finagleSettings: _*)
  .settings(inConfig(DebugTest)(Defaults.testSettings): _*)
  .settings(
    fork in DebugTest := true,
    excludeDependencies += "org.slf4j" % "slf4j-log4j12",
    mappings in (Compile, packageBin) ~= {
      _.filterNot(x => x._1.getName.equals("deployment.properties") || x._1.getName.equals("logback.xml"))
    },
    definedTests in DebugTest := (definedTests in Test).value,
    reColors := Seq("blue", "green", "magenta"),
    mainClass in reStart := Some("bookingtour.api.customers.Boot"),
    reStartArgs := Seq("config_path=/path_conf/wellness.conf"),
    Revolver.enableDebugging(port = 5562, suspend = false),
    version := "0.1",
    name := "customer-api",
    packMain := Map("customer-api" -> "bookingtour.api.customers.Boot"),
    packJvmOpts := Map(
      "customer-api" -> Seq(
        "-Dlogback.configurationFile=/path_conf/logback-customer-api.xml",
        "-Dconfig_path=/path_conf/wellness.conf",
        "-Djava.net.preferIPv4Stack=true",
        "-Dsun.reflect.inflationThreshold=2147483647",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseShenandoahGC",
        "-XX:+AlwaysPreTouch",
        "-XX:+DisableExplicitGC",
        "-XX:+ClassUnloadingWithConcurrentMark",
        "-XX:+UseCompressedOops",
        "-XX:ReservedCodeCacheSize=256m",
        "-XX:MaxMetaspaceSize=1G",
        "-XX:MaxInlineLevel=18",
        "-Xss8M",
        "-Xms1G",
        "-Xmx2G",
        "-server"
      )
    ),
    packGenerateWindowsBatFile := false,
    packJarNameConvention := "default",
    packGenerateMakefile := false,
    name := "customer-api"
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin, PackPlugin)

lazy val protocols = Seq(
  coreProtocol,
  apiProtocol,
  actorsProtocol,
  doobieProtocol,
  actorsProtocol,
  partiesProtocol,
  partiesProtocol,
  interLookOrdersProtocol,
  ordersProtocol,
  interLookProtocol,
  businessRuleProtocol,
  excursionsProtocol
)

lazy val cores = Seq(coreDoobie, coreKafka, coreFinch, coreActors)

lazy val datas = Seq(
  partiesData
)

lazy val services = Seq(
  partiesMs,
  apiCustomerMs
)

lazy val wellness = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(
    (
      protocols ++
        cores ++
        datas ++
        services
    ).map(x => x: ProjectReference): _*
  )
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin)
