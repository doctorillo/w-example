package bookingtour.core.actors.primitives.channel.basicf

import java.util.UUID

import cats.data.NonEmptyList
import zio.Ref

/**
  * © Alexey Toroshchin 2019.
  */
trait ProducerF[F[_], Value, Id] {
  type Consumer = ConsumerF[F, Value, Id]
  val consumers: Ref[Map[UUID, Consumer]]

  def channelCreate(id: UUID, consumer: Consumer): F[Unit]
  def channelDelete(): F[Unit]
  def emptySnapshot(): F[Unit]
  def snapshot(data: NonEmptyList[Value]): F[Unit]
  def itemCreate(data: NonEmptyList[Value]): F[Unit]
  def itemUpdate(data: NonEmptyList[Value]): F[Unit]
  def itemDelete(data: NonEmptyList[Value]): F[Unit]
}
