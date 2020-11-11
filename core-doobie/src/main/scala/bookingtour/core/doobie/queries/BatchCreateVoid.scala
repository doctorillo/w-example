package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.BatchCreateOps
import bookingtour.core.doobie.modules.{BatchCreateOps, DataModule}
import cats.Id
import doobie.implicits._
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait BatchCreateVoid[Input] {
  def run(in: List[Input])(cb: Either[List[Throwable], Unit] => Unit): Unit
}

object BatchCreateVoid {

  final def instance[Input](
      dataOps: BatchCreateOps[Input, _ <: Any],
      parN: Int = 1,
      parSize: Int = 500
  )(implicit dataModule: DataModule): BatchCreateVoid[Input] = new BatchCreateVoid[Input] {

    def run(in: List[Input])(cb: Either[List[Throwable], Unit] => Unit): Unit = {
      if (parN != 1) {
        val xs = in.grouped(parSize).toIterable
        dataModule.transactParallel(parN)(tx =>
          xs.map(input =>
            dataOps
              .runCreateList(input)
              .map(_ => ())
              .transact(tx)
          )
        )(cb)
      } else {
        dataModule.transact[Id, Unit](
          dataOps
            .runCreateList(in)
            .map(_ => ())
            .transact(_)
        )(cb)
      }

    }
  }

}
