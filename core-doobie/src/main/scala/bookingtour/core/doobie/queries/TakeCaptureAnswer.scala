package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.{DataModule, GetCaptureOps}
import bookingtour.core.doobie.modules.{DataModule, GetCaptureOps}
import bookingtour.protocols.core.db.{DbAnswer, DbEventPayload}
import doobie.implicits._
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait TakeCaptureAnswer[Ident, Version] {
  def run(in: Version)(
      cb: Either[List[Throwable], DbAnswer.Payload[DbEventPayload.BaseEntity[Ident, Version]]] => Unit
  ): Unit
}

object TakeCaptureAnswer {
  import bookingtour.protocols.core.types.FunctionKCore.instances.listDbAnswerFK

  final def instance[Ident, Version](dataOps: GetCaptureOps[Ident, Version])(
      implicit dataModule: DataModule
  ): TakeCaptureAnswer[Ident, Version] = new TakeCaptureAnswer[Ident, Version] {
    def run(
        in: Version
    )(
        cb: Either[List[Throwable], DbAnswer.Payload[DbEventPayload.BaseEntity[Ident, Version]]] => Unit
    ): Unit =
      dataModule.transact(
        dataOps
          .changes(in)
          .to[List]
          .map(listDbAnswerFK(_))
          .transact(_)
      )(cb)
  }

}
