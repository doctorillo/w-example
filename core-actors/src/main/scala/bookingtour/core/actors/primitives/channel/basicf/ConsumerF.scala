package bookingtour.core.actors.primitives.channel.basicf

import java.util.UUID

import cats.data.NonEmptyList

/**
  * © Alexey Toroshchin 2019.
  */
trait ConsumerF[F[_], Id, Value] {
  type Producer = ProducerF[F, Id, Value]
  val producer: zio.Ref[Producer]
  val channelId: UUID

  def channelCreated(id: UUID, producer: Producer): F[Unit]
  def channelDeleted(): F[Unit]
  def emptySnapshot(): F[Unit]
  def snapshot(data: NonEmptyList[Value]): F[Unit]
  def itemCreated(data: NonEmptyList[Value]): F[Unit]
  def itemUpdated(data: NonEmptyList[Value]): F[Unit]
  def itemDeleted(data: NonEmptyList[Value]): F[Unit]
}
