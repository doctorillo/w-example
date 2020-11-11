package bookingtour.core.actors.primitives.transforms.transform

import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.TransformChannel
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core.actors.channels.ChannelStatus
import cats.{Order, Semigroup}
import zio.Exit.{Failure, Success}
import zio.{Cause, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
protected[transform] trait State[Input, Value, Id] {
  _: Actor with ActorLogging =>

  protected val uniqueTag: String
  protected val producer: ActorProducer[Input, _]
  protected val makeChannelState: MakeChannel[Value, Id]
  protected val transform: TransformChannel[Input, Value]
  val zioRuntime: zio.Runtime[zio.ZEnv]
  protected val enableTrace: Boolean

  implicit val valueR: Value => Id
  implicit val idO: Order[Id]
  implicit val statusO: Order[ChannelStatus]
  implicit val statusSG: Semigroup[ChannelStatus]

  protected final val channelId0: UUID = UUID.randomUUID()

  protected final def channelTag0: String  = s"$uniqueTag:input"
  protected final val channelIdState: UUID = UUID.randomUUID()

  protected final def channelTagState: String = s"$uniqueTag:state"

  protected final def onChange(
      input: Any
  )(cb: Either[List[String], List[Value]] => Unit): Unit = {
    val effect = for {
      nec  <- ZIO.effect(input.asInstanceOf[List[Input]])
      data <- transform.run(uniqueTag, nec).map(_.distinct)
    } yield data
    zioRuntime.unsafeRunAsync(effect) {
      case Failure(cause: Cause[Throwable]) =>
        val errors = cause.failures.map(_.getMessage)
        errors.foreach(err => log.error(s"$uniqueTag. $err"))
        cb(Left(errors))

      case Success(chain) =>
        cb(Right(chain))
    }
  }

  protected final def shutdown(): Unit = {
    log.info(s"$uniqueTag. shutdown")
    context.stop(self)
  }
}
