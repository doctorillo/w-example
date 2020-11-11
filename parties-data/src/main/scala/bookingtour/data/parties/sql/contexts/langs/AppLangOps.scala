package bookingtour.data.parties.sql.contexts.langs

import java.util.UUID

import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.AppLangPREP
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AppLangOps private (
    getAllOps: GetAllOps[AppLangPREP],
    getByIdListOps: GetByIdListOps[UUID, AppLangPREP]
) extends GetAllOps[AppLangPREP] with GetByIdListOps[UUID, AppLangPREP] {
  def getAll(): Query0[AppLangPREP] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[AppLangPREP] = getByIdListOps.get(id)
}

object AppLangOps {
  final def apply(): GetAllOps[AppLangPREP] with GetByIdListOps[UUID, AppLangPREP] =
    new AppLangOps(AppLangGetAll(), AppLangGetById())
}
