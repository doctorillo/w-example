package bookingtour.protocols.properties.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.properties.env.CategoryMedicalDepartmentRepo
import zio.ZIO

/**
 * © Alexey Toroshchin 2019.
 */
final class LiveCategoryMedicalDepartmentRepo private (
  consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]
) extends CategoryMedicalDepartmentRepo {
  val categoryMedicalDepartmentRepo: CategoryMedicalDepartmentRepo.Service[Any] =
    () => consumer.all().catchAll(_ => ZIO.succeed(List.empty))
}

object LiveCategoryMedicalDepartmentRepo {
  final def apply(consumer: ConsumerAlg.Aux[Any, Int, EnumAPI]): CategoryMedicalDepartmentRepo =
    new LiveCategoryMedicalDepartmentRepo(consumer)
}
