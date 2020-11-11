package bookingtour.core.doobie.queries

import bookingtour.core.doobie.env.TakeByAnswerLayer
import bookingtour.core.doobie.modules.GetByIdListOps
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
final class LiveTakeByAnswer[Id, Value] private (dataOps: GetByIdListOps[Id, Value], tx: HikariTransactor[Task])
    extends TakeByAnswerLayer.Service[Id, Value] {
  def ask(in: List[Id]): Task[DbAnswer.Payload[Value]] = dataOps
    .get(in)
    .to[List]
    .map(listDbAnswerFK(_))
    .transact(tx)
}

object LiveTakeByAnswer {
  final def instance[Id, Value](
      dataOps: GetByIdListOps[Id, Value],
      tx: HikariTransactor[Task]
  ): TakeByAnswerLayer.Service[Id, Value] =
    new LiveTakeByAnswer(dataOps, tx)

  final def managed[Id, Value](
      dataOps: GetByIdListOps[Id, Value],
      tx: Managed[Throwable, HikariTransactor[Task]]
  ): Managed[Throwable, TakeByAnswerLayer.Service[Id, Value]] = tx.map(instance(dataOps, _))

  final def makeLayer[Id: Tag, Value: Tag](dataOps: GetByIdListOps[Id, Value])(
      implicit mtx: Managed[Throwable, HikariTransactor[Task]]
  ): ZLayer[Any, Throwable, Has[TakeByAnswerLayer.Service[Id, Value]]] =
    ZLayer.fromManaged(mtx.map(instance(dataOps, _)))
}
