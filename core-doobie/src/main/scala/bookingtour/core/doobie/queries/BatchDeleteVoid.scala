package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.BatchCreateOps
import bookingtour.core.doobie.modules.{BatchCreateOps, DataModule}
import cats.Id
import doobie.implicits._
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait BatchDeleteVoid[Input] {
  def run(in: List[Input])(cb: Either[List[Throwable], Unit] => Unit): Unit
}

object BatchDeleteVoid {

  final def instance[Input](
      dataOps: BatchCreateOps[Input, _ <: Any]
  )(implicit dataModule: DataModule): BatchDeleteVoid[Input] = new BatchDeleteVoid[Input] {
    def run(in: List[Input])(cb: Either[List[Throwable], Unit] => Unit): Unit = {
      dataModule.transact[Id, Unit](
        dataOps
          .runCreateList(in)
          .map(_ => ())
          .transact(_)
      )(cb)
    }
  }

}
