package bookingtour.protocols.properties.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.properties.env.CategoryTreatmentIndicationRepo
import zio.ZIO

/**
 * © Alexey Toroshchin 2019.
 */
final class LiveCategoryTreatmentIndicationRepo private (
  consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends CategoryTreatmentIndicationRepo {
  val categoryTreatmentIndicationRepo: CategoryTreatmentIndicationRepo.Service[Any] =
    () => consumer.all().catchAll(_ => ZIO.succeed(List.empty)).map(_.distinct)
}

object LiveCategoryTreatmentIndicationRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): CategoryTreatmentIndicationRepo =
    new LiveCategoryTreatmentIndicationRepo(consumer)
}
