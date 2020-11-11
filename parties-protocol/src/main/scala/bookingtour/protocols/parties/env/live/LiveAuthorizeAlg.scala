package bookingtour.protocols.parties.env.live

import java.time.Instant

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.values.{UserName, UserPassword}
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.agg.basic.SolverDataAgg
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import bookingtour.protocols.parties.env.AuthorizeAlg
import bookingtour.protocols.parties.newTypes.Email
import cats.instances.all._
import cats.syntax.order._
import com.github.t3hnar.bcrypt._
import eu.timepit.refined.types.string.NonEmptyString
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveAuthorizeAlg private (
    implicit solverAlg: ConsumerAlg.Aux[Any, Int, SolverDataAgg]
) extends AuthorizeAlg {
  val authorizeAlg: AuthorizeAlg.Service[Any] = new AuthorizeAlg.Service[Any] {
    private final def fetchSolver(
        email: String,
        password: String
    ): URIO[Any, Option[SolverDataAgg]] =
      for {
        a <- ZIO
              .fromEither(NonEmptyString.from(email))
              .fold(_ => List.empty, x => List(UserName(x)))
        b <- ZIO
              .fromEither(NonEmptyString.from(password))
              .fold(_ => List.empty, x => List(UserPassword(x)))
        c <- ZIO
              .fromOption(a.headOption.zip(b.headOption).headOption)
              .fold(_ => List.empty, List(_))
        d <- ZIO
              .fromOption(c.headOption)
              .foldM(
                _ => ZIO.none,
                x =>
                  solverAlg
                    .byValue(
                      condition = z => z.email === Email(x._1.x.value) && x._2.x.value.isBcrypted(z.password.x)
                    )
                    .map(_.headOption)
                    .catchAll(_ => ZIO.none)
              )
      } yield d

    def authorize(
        name: String,
        password: String
    ): URIO[Any, Option[ContextEnvUI]] =
      (for {
        solverOpt <- fetchSolver(name, password)
        _         <- ZIO.when(solverOpt.isEmpty)(ZIO.fail("solver not found."))
        ctx       = solverOpt.map(x => SolverDataAgg.toContextEnvUI(LangItem.Ru, Instant.now(), Instant.now(), x))
      } yield ctx).catchAll(_ => ZIO.none)
  }
}

object LiveAuthorizeAlg {
  final def apply()(
      implicit solverAlg: ConsumerAlg.Aux[Any, Int, SolverDataAgg]
  ): AuthorizeAlg = new LiveAuthorizeAlg
}
