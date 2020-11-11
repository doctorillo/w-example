package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.{DataModule, GetAllOps}
import bookingtour.protocols.core.db.DbAnswer.Payload
import doobie.implicits._
import zio.ZIO
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2019.
  */
trait TakeAnswer[Value] {
  def run(cb: Either[List[Throwable], Payload[Value]] => Unit): Unit
}

object TakeAnswer {
  import bookingtour.protocols.core.types.FunctionKCore.instances.listDbAnswerFK

  final def instance[Value](
      dataOps: GetAllOps[Value]
  )(implicit dataModule: DataModule): TakeAnswer[Value] =
    (cb: Either[List[Throwable], Payload[Value]] => Unit) =>
      dataModule.transact(
        dataOps
          .getAll()
          .to[List]
          .map(listDbAnswerFK(_))
          .transact(_)
      )(cb)

  final def instanceWithMap[Container, Value](
      dataOps: GetAllOps[Container]
  )(implicit dataModule: DataModule, fn: Container => Value): TakeAnswer[Value] =
    (cb: Either[List[Throwable], Payload[Value]] => Unit) => {
      dataModule.transact(tx =>
        for {
          a <- dataOps
                .getAll()
                .to[List]
                .transact(tx)
                .onError(_ => ZIO.succeed(List.empty))
          b <- ZIO.effect(a.map(fn)).map(listDbAnswerFK(_))
        } yield b
      )(cb)
    }
}
