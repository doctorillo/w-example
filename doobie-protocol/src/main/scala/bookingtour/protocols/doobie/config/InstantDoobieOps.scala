package bookingtour.protocols.doobie.config

import java.time.Instant

import doobie.util.meta.Meta

/**
  * © Alexey Toroshchin 2019.
  */
trait InstantDoobieOps {
  implicit final val instantM: Meta[Instant] = doobie.implicits.javatime.JavaTimeInstantMeta
}

object InstantDoobieOps {
  def apply(): InstantDoobieOps = new InstantDoobieOps {}
}
