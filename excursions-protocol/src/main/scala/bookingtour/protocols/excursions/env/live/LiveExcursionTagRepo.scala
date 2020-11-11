package bookingtour.protocols.excursions.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.excursions.env.ExcursionTagRepo
import zio.ZIO

/**
  * © Alexey Toroshchin 2020.
  */
final class LiveExcursionTagRepo private (
    consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends ExcursionTagRepo {
  val excursionTagRepo: ExcursionTagRepo.Service[Any] = () => consumer.all().catchAll(_ => ZIO.succeed(List.empty))
}

object LiveExcursionTagRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): ExcursionTagRepo =
    new LiveExcursionTagRepo(consumer)
}
