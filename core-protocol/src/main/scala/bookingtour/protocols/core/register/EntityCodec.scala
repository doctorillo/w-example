package bookingtour.protocols.core.register

import cats.effect.Sync
import cats.Applicative
import io.circe.{Decoder, Encoder}
import tofu.Raise
import tofu.syntax.monadic._

import scala.util.{Failure, Success, Try}

/**
  * © Alexey Toroshchin 2020.
  */
trait EntityCodec[F[_]] {
  type Result

  val key: RegisterKey

  def from(x: String): F[Result]
  def to(x: Result): F[String]
}

object EntityCodec {
  final type Aux[F[_], A] = EntityCodec[F] {
    type Result = A
  }

  final def apply[F[_]: Sync, A](implicit ev: Aux[F, A]): F[Aux[F, A]] = Sync[F].delay(ev)
}

final class LiveCirceEntityCodec[F[_]: Applicative, A: Encoder: Decoder] private (val key: RegisterKey)(
    implicit F: Raise[F, Throwable]
) extends EntityCodec[F] {

  type Result = A

  def from(x: String): F[Result] = io.circe.parser.decode[A](x) match {
    case Left(thr) =>
      F.raise(thr)
    case Right(value) =>
      value.pure[F]
  }

  def to(x: A): F[String] = Try(Encoder[A].apply(x).noSpaces) match {
    case Failure(thr) =>
      F.raise(thr)

    case Success(value) =>
      value.pure[F]
  }
}

object LiveCirceEntityCodec {
  import bookingtour.protocols.core.types.WeakTypeOps._

  import scala.reflect.runtime.universe._

  final def apply[F[_]: Applicative, A: Encoder: Decoder: WeakTypeTag](version: Int = 0, constant: Boolean = false)(
      implicit F: Raise[F, Throwable]
  ): LiveCirceEntityCodec[F, A] = new LiveCirceEntityCodec(
    RegisterKey(
      typeTag = weakT[A],
      constant = constant
    )
  )
}
