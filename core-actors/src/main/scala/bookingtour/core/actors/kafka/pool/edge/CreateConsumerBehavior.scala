package bookingtour.core.actors.kafka.pool.edge

import akka.Done
import akka.actor.{Actor, ActorLogging, Stash}
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Keep
import bookingtour.core.actors.kafka.pool.worker.KafkaConsumerWorker
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.{EdgeHeartbeat, StreamGroup, TopicConsumerWorker}
import bookingtour.protocols.core.register.RegisterEntity

/**
  * © Alexey Toroshchin 2019.
  */
protected[edge] trait CreateConsumerBehavior {
  _: Actor with Stash with ActorLogging with BasicBehavior =>

  protected final def createConsumerBehavior(
      uniqueTag: String,
      mainTopic: String,
      heartbeatTopic: String,
      consumerSettings: ConsumerSettings[String, String],
      topics: Set[StreamGroup],
      heartbeatInterval: Long,
      enableTrace: Boolean
  )(
      implicit m: Materializer,
      r: zio.Runtime[zio.ZEnv],
      p: MessageProducer[Done],
      e: RegisterEntity.Aux[EdgeHeartbeat]
  ): Unit = {
    val tag = s"$uniqueTag. create-consumer-behavior"
    if (enableTrace) {
      log.info(s"$tag. enter.")
    }
    val committerSettings = CommitterSettings(context.system)
    val workers = topics.map { sg =>
      val worker = KafkaConsumerWorker
        .make(
          uniqueTag = s"$uniqueTag:${sg.topic}:worker",
          topic = sg.topic,
          enableTrace = enableTrace
        )
      val flow = UnmarshalFlow
        .make(topic = sg.topic, worker = worker, log = log, enableTrace = enableTrace)
      val control: DrainingControl[Done] = Consumer
        .committableSource(
          settings = consumerSettings.withGroupId(sg.groupId),
          subscription = Subscriptions.topics(sg.topic)
        )
        .via(flow)
        .toMat(Committer.sink(committerSettings))(Keep.both)
        .mapMaterializedValue(DrainingControl.apply)
        .run()

      if (enableTrace) {
        log.info(s"$tag. consumer for ${sg.topic} created with group-id: ${sg.groupId}.")
      }
      TopicConsumerWorker(
        groupId = sg.groupId,
        topic = sg.topic,
        consumer = control,
        worker = worker.x
      )
    }
    unstashAll()
    context.become(
      basicBehavior(
        uniqueTag = uniqueTag,
        mainTopic = mainTopic,
        heartbeatTopic = heartbeatTopic,
        consumers = workers,
        producers = List.empty,
        heartbeats = List.empty,
        heartbeatInterval = heartbeatInterval,
        enableTrace = enableTrace
      )
    )
  }
}
