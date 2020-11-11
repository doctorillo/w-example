package bookingtour.protocols.core.types

/**
  * © Alexey Toroshchin 2019.
  */
trait Capture[-F[_]] {
  def continue[A](k: F[A]): A
}

object Capture {
  type Constructors[F[_]] = F[Capture[F]]

  type Arbitrary

  def apply[F[_]]: Capture.Apply[F] = new Apply[F]

  final class Apply[F[_]] {
    def apply(f: F[Arbitrary] => Arbitrary): Capture[F] = new Capture[F] {
      def continue[A](k: F[A]): A = f(k.asInstanceOf[F[Arbitrary]]).asInstanceOf[A]
    }
  }
}
