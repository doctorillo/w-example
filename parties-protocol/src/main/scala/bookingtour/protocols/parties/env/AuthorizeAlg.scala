package bookingtour.protocols.parties.env

import bookingtour.protocols.parties.api.ui.ContextEnvUI
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait AuthorizeAlg extends Serializable {
  val authorizeAlg: AuthorizeAlg.Service[Any]
}

object AuthorizeAlg {
  trait Service[R] extends Serializable {
    def authorize(
        name: String,
        password: String
    ): URIO[R, Option[ContextEnvUI]]
  }

  final object > {
    def authorize(
        name: String,
        password: String
    ): URIO[AuthorizeAlg, Option[ContextEnvUI]] =
      ZIO.accessM[AuthorizeAlg](_.authorizeAlg.authorize(name, password))
  }
}
