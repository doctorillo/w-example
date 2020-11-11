package bookingtour.protocols.properties.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.properties.env.CategoryRoomFacilityRepo
import zio.ZIO

/**
 * © Alexey Toroshchin 2019.
 */
final class LiveCategoryRoomFacilityRepo private (
  consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends CategoryRoomFacilityRepo {
  val categoryRoomFacilityRepo: CategoryRoomFacilityRepo.Service[Any] =
    () => consumer.all().catchAll(_ => ZIO.succeed(List.empty))
}

object LiveCategoryRoomFacilityRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): CategoryRoomFacilityRepo =
    new LiveCategoryRoomFacilityRepo(consumer)
}
