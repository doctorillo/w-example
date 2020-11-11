package bookingtour.protocols.core.register

import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
trait RegisterEntity {
  type Result
  val key: RegisterKey

  final def cast(x: Any): ZIO[Any, String, Result] =
    ZIO.effect(x.asInstanceOf[Result]).catchAll(thr => ZIO.fail(s"cast. $x. ${thr.getMessage}."))

  def decode(input: String): ZIO[Any, String, Result]

  def encode(input: Result): ZIO[Any, String, String]
}

object RegisterEntity {
  import scala.reflect.runtime.universe._

  import io.circe.{Decoder, Encoder}

  final type Aux[A] = RegisterEntity { type Result = A }

  final def apply[T: Encoder: Decoder: WeakTypeTag](
      version: Int = 0,
      constant: Boolean = false
  ): RegisterEntity.Aux[T] = RegisterEntityCirce[T]()

  implicit def applyI[A: Encoder: Decoder: WeakTypeTag]: RegisterEntity.Aux[A] = apply()
}
