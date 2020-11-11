package bookingtour.protocols.core.types

import scala.reflect.runtime.universe._

/**
  * © Alexey Toroshchin 2019.
  */
object WeakTypeOps {
  final def weakT[T](implicit attag: WeakTypeTag[T]): String =
    weakTypeTag[T].tpe match {
      case TypeRef(_, sym, args) =>
        args.map(_.baseClasses.head.fullName.toString).foldLeft(sym.fullName)((a, x) => s"$a.$x")
    }
}
