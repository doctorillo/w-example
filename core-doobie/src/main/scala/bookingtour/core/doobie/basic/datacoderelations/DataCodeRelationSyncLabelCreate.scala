package bookingtour.core.doobie.basic.datacoderelations

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{DataCodeRelationE, LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.SyncItem
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataCodeRelationSyncLabelCreate private (
    table: String,
    getOps: GetOps[UUID, DataCodeRelationE],
    syncOps: ByInputOps[SyncItem, SyncE] with CreateOps[SyncE.Create, SyncE] with BatchCreateOps[SyncE.Create, SyncE],
    labelOps: ByInputOps[LabelE.Create, LabelE]
      with CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE]
) extends CreateOps[DataCodeRelationE.CreateWithSync, DataCodeRelationE]
    with BatchCreateOps[DataCodeRelationE.CreateWithSync, DataCodeRelationE] {

  def insert(item: DataCodeRelationE.CreateWithSync): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "code") VALUES (${item.solverId}, ${item.dataId}, ${item.code})""").update

  def runCreate(data: DataCodeRelationE.CreateWithSync): ConnectionIO[DataCodeRelationE] = {
    for {
      a <- syncOps.byInput(data.sync).option
      b <- a match {
            case Some(value) =>
              getOps.get(value.dataId).unique

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- syncOps.runCreate(SyncE.Create(dataId = id, sync = data.sync))
                _  <- labelOps.runCreateList(LabelE.create(id, data.code))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object DataCodeRelationSyncLabelCreate {
  final def apply(
      tableDataCode: String,
      tableSync: String,
      tableLabel: String
  ): DataCodeRelationSyncLabelCreate =
    new DataCodeRelationSyncLabelCreate(
      tableDataCode,
      DataCodeRelationGet(tableDataCode),
      SyncCreate(tableSync),
      LabelCreate(tableLabel)
    )
}
