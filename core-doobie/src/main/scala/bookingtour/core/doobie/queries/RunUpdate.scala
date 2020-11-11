package bookingtour.core.doobie.queries

import bookingtour.core.doobie.modules.{DataModule, UpdateOps}
import doobie.implicits._
import zio.Task
import zio.interop.catz._

/**
  * © Alexey Toroshchin 2020.
  */
trait RunUpdate[Input, Output] {
  def run(in: Input): Task[Output]
}

object RunUpdate {

  final def instance[Input, Output](
      dataOps: UpdateOps[Input, Output]
  )(implicit dataModule: DataModule): RunUpdate[Input, Output] = (in: Input) => {
    dataOps
      .runUpdate(in)
      .transact(dataModule.xa)
  }

}
