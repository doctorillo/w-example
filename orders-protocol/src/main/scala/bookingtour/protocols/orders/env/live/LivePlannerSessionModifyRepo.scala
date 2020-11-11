package bookingtour.protocols.orders.env.live

import bookingtour.core.doobie.modules.{DataModule, UpdateOps}
import bookingtour.core.doobie.queries.RunUpdate
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.env.PlannerSessionModifyRepo
import com.typesafe.scalalogging.Logger
import zio.ZIO

/**
  * © Alexey Toroshchin 2020.
  */
final class LivePlannerSessionModifyRepo private (modify: RunUpdate[PlannerSession, PlannerSession])(
    implicit log: Logger
) extends PlannerSessionModifyRepo {
  val plannerSessionModifyRepo: PlannerSessionModifyRepo.Service[Any] = (session: PlannerSession) =>
    modify.run(session).map(List(_)).catchAll { thr =>
      log.error("planner-session-modify-repo. {}", thr)
      ZIO.succeed(List(session))
    }
}

object LivePlannerSessionModifyRepo {
  final def apply()(
      implicit dataModule: DataModule,
      dataOps: UpdateOps[PlannerSession, PlannerSession],
      log: Logger
  ): PlannerSessionModifyRepo = new LivePlannerSessionModifyRepo(RunUpdate.instance(dataOps))
}
