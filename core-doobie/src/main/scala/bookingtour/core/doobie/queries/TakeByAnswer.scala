package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{DataModule, GetByIdListOps}
import bookingtour.protocols.core.db.DbAnswer.Payload
import doobie.implicits._
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait TakeByAnswer[Ident, Value] {
  def run(in: List[Ident])(cb: Either[List[Throwable], Payload[Value]] => Unit): Unit
}

object TakeByAnswer {
  import bookingtour.protocols.core.types.FunctionKCore.instances.listDbAnswerFK

  final def instance[Ident, Value](
      dataOps: GetByIdListOps[Ident, Value]
  )(implicit dataModule: DataModule): TakeByAnswer[Ident, Value] = new TakeByAnswer[Ident, Value] {
    def run(in: List[Ident])(cb: Either[List[Throwable], Payload[Value]] => Unit): Unit =
      dataModule.transact(
        dataOps
          .get(in)
          .to[List]
          .map(listDbAnswerFK(_))
          .transact(_)
      )(cb)
  }
  final def instanceWithMap[Ident, Container, Value](
      dataOps: GetByIdListOps[Ident, Container]
  )(implicit dataModule: DataModule, fn: Container => Value): TakeByAnswer[Ident, Value] =
    new TakeByAnswer[Ident, Value] {
      def run(in: List[Ident])(cb: Either[List[Throwable], Payload[Value]] => Unit): Unit =
        dataModule.transact(
          dataOps
            .get(in)
            .to[List]
            .map(xs => listDbAnswerFK(xs.map(fn)))
            .transact(_)
        )(cb)
    }
}
