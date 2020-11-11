package bookingtour.core.doobie.basic.datavaluecoderelations

import java.util.UUID

import bookingtour.core.doobie.basic.descriptions.DescriptionCreate
import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{DataValueCodeRelationE, DescriptionE, LabelE, SyncE}
import bookingtour.protocols.core.values.enumeration.SyncItem
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataValueCodeRelationSyncLabelDescriptionCreate private (
    table: String,
    getOps: GetOps[UUID, DataValueCodeRelationE],
    syncOps: ByInputOps[SyncItem, SyncE] with CreateOps[SyncE.Create, SyncE] with BatchCreateOps[SyncE.Create, SyncE],
    labelOps: ByInputOps[LabelE.Create, LabelE]
      with CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE],
    descriptionOps: ByInputOps[DescriptionE.Create, DescriptionE]
      with CreateOps[DescriptionE.Create, DescriptionE] with BatchCreateOps[DescriptionE.Create, DescriptionE]
) extends CreateOps[DataValueCodeRelationE.CreateWithSync, DataValueCodeRelationE]
    with BatchCreateOps[DataValueCodeRelationE.CreateWithSync, DataValueCodeRelationE] {

  def insert(item: DataValueCodeRelationE.CreateWithSync): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "value_id", "code") VALUES (${item.solverId}, ${item.dataId}, ${item.valueId}, ${item.code})""").update

  def runCreate(
      data: DataValueCodeRelationE.CreateWithSync
  ): ConnectionIO[DataValueCodeRelationE] = {
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
                _  <- descriptionOps.runCreateList(DescriptionE.create(id, data.code))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object DataValueCodeRelationSyncLabelDescriptionCreate {
  final def apply(
      tableDataValueCode: String,
      tableSync: String,
      tableLabel: String,
      tableDescription: String
  ): CreateOps[DataValueCodeRelationE.CreateWithSync, DataValueCodeRelationE]
    with BatchCreateOps[DataValueCodeRelationE.CreateWithSync, DataValueCodeRelationE] =
    new DataValueCodeRelationSyncLabelDescriptionCreate(
      tableDataValueCode,
      DataValueCodeRelationGet(tableDataValueCode),
      SyncCreate(tableSync),
      LabelCreate(tableLabel),
      DescriptionCreate(tableDescription)
    )
}
