package bookingtour.protocols.properties.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.properties.env.CategoryAmenityRepo
import zio.ZIO

/**
 * © Alexey Toroshchin 2019.
 */
final class LiveCategoryAmenityRepo private (
  consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends CategoryAmenityRepo {
  val categoryAmenityRepo: CategoryAmenityRepo.Service[Any] = () =>
    consumer.all().catchAll(_ => ZIO.succeed(List.empty))
}

object LiveCategoryAmenityRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): CategoryAmenityRepo =
    new LiveCategoryAmenityRepo(consumer)
}
