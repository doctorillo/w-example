package bookingtour.core.doobie.queries

import bookingtour.core.doobie.env.TakeAnswerLayer
import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.protocols.core.db.DbAnswer
import doobie.hikari.HikariTransactor
import zio.{Has, Managed, Task, ZLayer}
import bookingtour.protocols.core.types.FunctionKCore.instances.listDbAnswerFK
import doobie.implicits._
import izumi.reflect.Tags.Tag
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
final class LiveTakeAnswer[A] private (dataOps: GetAllOps[A], tx: HikariTransactor[Task])
    extends TakeAnswerLayer.Service[A] {
  def ask: Task[DbAnswer.Payload[A]] = dataOps
    .getAll()
    .to[List]
    .map(listDbAnswerFK(_))
    .transact(tx)
}

object LiveTakeAnswer {
  final def instance[A](dataOps: GetAllOps[A], tx: HikariTransactor[Task]): TakeAnswerLayer.Service[A] =
    new LiveTakeAnswer(dataOps, tx)

  final def managed[A](
      dataOps: GetAllOps[A],
      tx: Managed[Throwable, HikariTransactor[Task]]
  ): Managed[Throwable, TakeAnswerLayer.Service[A]] = tx.map(new LiveTakeAnswer(dataOps, _))

  final def makeLayer[A: Tag](dataOps: GetAllOps[A])(
      implicit mtx: Managed[Throwable, HikariTransactor[Task]]
  ): ZLayer[Any, Throwable, Has[TakeAnswerLayer.Service[A]]] = ZLayer.fromManaged(mtx.map(instance(dataOps, _)))
}
