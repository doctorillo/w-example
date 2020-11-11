package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.{CreateOps, DataModule}
import doobie.implicits._
import zio.Task
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait RunCreate[Input, Output] {
  def run(in: Input): Task[Output]
}

object RunCreate {

  final def instance[Input, Output](
      dataOps: CreateOps[Input, Output]
  )(implicit dataModule: DataModule): RunCreate[Input, Output] = (in: Input) => {
    dataOps
      .runCreate(in)
      .transact(dataModule.xa)
  }

}
