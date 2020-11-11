package bookingtour.protocols.core.register

import scala.reflect.runtime.universe._

import bookingtour.protocols.core.types.WeakTypeOps._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class RegisterEntityCirce[Value] private (val key: RegisterKey)(
    implicit encoder: Encoder[Value],
    decoder: Decoder[Value]
) extends RegisterEntity {
  type Result = Value

  def decode(input: String): ZIO[Any, String, Value] =
    io.circe.parser.decode[Result](input) match {
      case Left(err) =>
        ZIO.fail(s"decode. ${err.fillInStackTrace().getMessage}")

      case Right(result) =>
        ZIO.succeed(result)
    }

  def encode(input: Value): ZIO[Any, String, String] =
    ZIO.effect(input.asJson.noSpaces).catchAll(thr => ZIO.fail(s"encode. ${thr.getMessage}."))
}

object RegisterEntityCirce {
  final def apply[Value](
      version: Int = 0,
      constant: Boolean = false
  )(
      implicit encoder: Encoder[Value],
      decoder: Decoder[Value],
      attag: WeakTypeTag[Value]
  ): RegisterEntity.Aux[Value] = {
    val key: RegisterKey = RegisterKey(
      typeTag = weakT[Value],
      constant = constant
    )
    new RegisterEntityCirce[Value](key)
  }
}
