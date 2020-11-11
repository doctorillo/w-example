package bookingtour.core.actors.modules

import akka.actor.ActorSystem
import akka.stream.Materializer
import bookingtour.protocols.core.modules.BaseModule

/**
  * © Alexey Toroshchin 2019.
  */
trait AkkaModule {
  implicit val actorSystem: ActorSystem
  implicit val materializer: Materializer
}

object AkkaModule {
  final def apply(service: String)(implicit bm: BaseModule): AkkaModule = {
    val n                        = bm.appConfig.getString(s"ms.$service.actorSystemName")
    val c                        = bm.appConfig.getConfig(s"ms.$service").withOnlyPath("akka")
    implicit val as: ActorSystem = ActorSystem(n, c)
    val m                        = Materializer.createMaterializer(as)
    new AkkaModule {
      implicit final val actorSystem: ActorSystem   = as
      implicit final val materializer: Materializer = m
    }
  }
}
