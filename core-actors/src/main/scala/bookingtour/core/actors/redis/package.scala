package bookingtour.core.actors

import cats.data.NonEmptyChain
import com.twitter.util.Future

/**
  * © Alexey Toroshchin 2019.
  */
package object redis {
  final case class W[A](xs: NonEmptyChain[A])

  final def runFuture(f: Future[Unit])(cb: Either[Throwable, Unit] => Unit): Unit = {
    f.onSuccess(_ => cb(Right(())))
    f.onFailure(thr => cb(Left(thr)))
  }
}
