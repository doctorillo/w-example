package bookingtour.protocols.doobie.types

import scala.reflect.runtime.universe.TypeTag

import cats.data.{Chain, NonEmptyList}
import cats.syntax.either._
import com.typesafe.scalalogging.Logger
import doobie.util.{Get, Put}
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.postgresql.util.PGobject

/**
  * © Alexey Toroshchin 2019.
  */
object JsonbToJson {
  private final val typ: NonEmptyList[String] = NonEmptyList.one("jsonb")

  private final def lm[A: Decoder](x: PGobject): A =
    decode[A](x.getValue).leftMap[A](e => throw e).merge

  final def classGet[A: TypeTag: Decoder]: Get[A] = Get.Advanced.other[PGobject](typ).map(lm[A])

  final def classPut[A: TypeTag: Encoder]: Put[A] = Put.Advanced.other[PGobject](typ).contramap { x =>
    val o = new PGobject
    o.setType(typ.head)
    o.setValue(x.asJson.noSpaces)
    o
  }

  final def stringGet[A: TypeTag: Decoder]: Get[A] =
    Get.Advanced
      .other[String](typ)
      .map[A](a => decode[A](a).leftMap[A](e => throw e).merge)

  final def stringPut[A: TypeTag: Encoder]: Put[A] =
    Put.Advanced
      .other[String](typ)
      .contramap[A](_.asJson.noSpaces)

  final def arrJson[A: Decoder](xs: Array[String])(implicit log: Logger): Chain[A] =
    xs.foldLeft(Chain.empty[A])((a, x) =>
      io.circe.parser
        .decode[A](x)
        .fold(err => {
          log.error("arrJson. {}", err.fillInStackTrace())
          a
        }, value => a :+ value)
    )
}
