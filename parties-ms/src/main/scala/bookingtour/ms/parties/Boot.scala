package bookingtour.ms.parties

import bookingtour.core.actors.modules._
import bookingtour.core.doobie.modules.{DataModule, TransactorModule}
import bookingtour.ms.parties.modules._
import bookingtour.protocols.core.actors.kafka.StreamGroup
import bookingtour.protocols.core.modules.{BaseModule, RuntimeModule}
import cats.data.NonEmptySet
import doobie.hikari.HikariTransactor
import zio.{Managed, Task}

/**
  * © Alexey Toroshchin 2019.
  */
object Boot extends App {
  val path: String = args.toList.flatMap { x =>
    x.split("=").toList.map(_.trim) match {
      case "config_path" :: z :: Nil =>
        List(z)
      case _ =>
        List.empty
    }
  }.head
  private final val serviceTag: String      = "parties"
  implicit private final val bm: BaseModule = BaseModule(fileName = path, service = serviceTag)
  import bm._
  implicit private final val sm: StateEnvModule = StateEnvModule(serviceTag)
  implicit private final val rm: RuntimeModule  = RuntimeModule.default(enableTrace)
  import rm._
  implicit private final val am: AkkaModule           = AkkaModule(serviceTag)
  implicit private final val ktm: KafkaTopicModule    = KafkaTopicModule()
  implicit private final val kpm: KafkaProducerModule = KafkaProducerModule()
  import ktm._
  implicit private final val kem: KafkaEdgeModule = KafkaEdgeModule(
    uniqueTag = serviceTag,
    mainTopic = topics.partiesTopic,
    consumeTopics = NonEmptySet.of(
      StreamGroup(topic = topics.partiesWatchTopic, groupId = s"$nodeId-$serviceTag-kt-0"),
      StreamGroup(topic = topics.partiesTopic, groupId = s"$nodeId-$serviceTag-kt-1"),
      StreamGroup(topic = topics.heartbeatTopic, groupId = s"$nodeId-$serviceTag-kt-2")
    )
  )
  implicit private final val dm: DataModule = DataModule(serviceTag, 60000L)
  implicit private final val mtx: Managed[Throwable, HikariTransactor[Task]] = TransactorModule
    .make(config = appConfig, serviceTag, zioRuntime.platform.executor.asEC, blockingEC, 6000)
  import kem._
  implicit private final val arrows: PartyArrows = PartyArrows()
  implicit private final val eo: DataOps         = DataOps()
  implicit private final val dco: ConsumerOps    = ConsumerOps()
  implicit private final val ao: AggregateOps    = AggregateOps()
  ProducerOps()
  UpsertOps()
}
