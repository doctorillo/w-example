package bookingtour.protocols.core.types

/**
  * © Alexey Toroshchin 2019.
  */
object CompareOps {
  import cats.instances.int._
  import cats.syntax.order._

  final type CMP = () => Int

  final def compareFn(xs: Int*): Int =
    xs.foldLeft(0) { (acc, x) =>
      if (acc =!= 0) {
        acc
      } else
        x
    }

  final def lazyCompareOps(xs: CMP*): Int =
    xs.foldLeft(0) { (acc, x) =>
      if (acc =!= 0) {
        acc
      } else
        x()
    }
}
