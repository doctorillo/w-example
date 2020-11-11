package bookingtour.core.doobie.basic.enumvalues

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.{EnumProjectionE, LabelE}
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class EnumProjectionWithLabelCreate private (
    getOps: GetOps[UUID, EnumProjectionE],
    createOps: ByInputOps[EnumProjectionE.Create, EnumProjectionE]
      with CreateOps[EnumProjectionE.Create, EnumProjectionE]
      with BatchCreateOps[EnumProjectionE.Create, EnumProjectionE],
    labelOps: BatchCreateOps[LabelE.Create, LabelE]
) extends CreateOps[EnumProjectionE.CreateWithLabel, EnumProjectionE]
    with BatchCreateOps[EnumProjectionE.CreateWithLabel, EnumProjectionE] with EnumerationToDoobieOps {

  def insert(item: EnumProjectionE.CreateWithLabel): Update0 =
    createOps.insert(EnumProjectionE.itemT0(item))

  def runCreate(data: EnumProjectionE.CreateWithLabel): ConnectionIO[EnumProjectionE] = {
    for {
      a <- createOps.byInput(EnumProjectionE.itemT0(data)).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- labelOps.runCreateList(LabelE.create(id, data.name))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object EnumProjectionWithLabelCreate {
  final def apply(
      tableEnum: String,
      tableLabel: String
  ): CreateOps[EnumProjectionE.CreateWithLabel, EnumProjectionE]
    with BatchCreateOps[EnumProjectionE.CreateWithLabel, EnumProjectionE] =
    new EnumProjectionWithLabelCreate(
      EnumProjectionGet(tableEnum),
      EnumProjectionCreate(tableEnum),
      LabelCreate(tableLabel)
    )
}
