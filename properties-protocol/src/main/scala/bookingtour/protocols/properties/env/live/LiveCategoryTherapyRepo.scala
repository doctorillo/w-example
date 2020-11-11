package bookingtour.protocols.properties.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.properties.env.CategoryTherapyRepo
import zio.ZIO

/**
 * © Alexey Toroshchin 2019.
 */
final class LiveCategoryTherapyRepo private (
  consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends CategoryTherapyRepo {
  val categoryTherapyRepo: CategoryTherapyRepo.Service[Any] = () =>
    consumer.all().catchAll(_ => ZIO.succeed(List.empty))
}

object LiveCategoryTherapyRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): CategoryTherapyRepo =
    new LiveCategoryTherapyRepo(consumer)
}
