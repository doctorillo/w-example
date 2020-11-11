package bookingtour.core.finch

import java.util.UUID

import ActiveEndpoints.instances._
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.parties.api.sessions.EnvApi
import bookingtour.protocols.parties.api.sessions.EnvApi.{Authorized, Guest}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future
import com.typesafe.scalalogging.Logger
import io.estatico.newtype.ops._
import zio.{Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class ContractFilter private (
    fetch: (SessionIdValue, FingerprintValue, RemoteValue) => Task[EnvApi],
    enableTrace: Boolean
)(implicit runtime: zio.Runtime[zio.ZEnv], log: Logger)
    extends SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    import ContractFilter._
    val headerEffect = for {
      a <- ZIO
            .fromOption(
              request.headerMap
                .get(sessionHeader)
            )
            .map(UUID.fromString(_).coerce[SessionIdValue])
            .catchAll(_ => ZIO.fail(SessionNotFound))
      b <- ZIO
            .fromOption(
              request.headerMap
                .get(fingerprintHeader)
                .filterNot(_.trim.isEmpty)
            )
            .map(_.coerce[FingerprintValue])
            .catchAll(_ => ZIO.fail(FingerprintNotFound))
      c <- ZIO
            .effect(request.remoteAddress.getHostAddress.coerce[RemoteValue])
            .catchAll(_ => ZIO.fail(RemoteNotFound))
      d <- fetch(a, b, c).catchAll(_ => ZIO.fail(SolverNotFound))
    } yield d
    runtime.unsafeRunSync(headerEffect) match {
      case zio.Exit.Failure(cause) =>
        cause.failures match {
          case Nil =>
            Future.value(Response(Status.BadRequest))

          case SessionNotFound :: _ =>
            Future.value(Response(Status.BadRequest))

          case FingerprintNotFound :: _ =>
            Future.value(Response(Status.BadRequest))

          case RemoteNotFound :: _ =>
            Future.value(Response(Status.BadRequest))

          case RemoteNotFound :: _ =>
            Future.value(Response(Status.BadRequest))

          case SolverNotFound :: _ =>
            Future.value(Response(Status.EnhanceYourCalm))
        }

      case zio.Exit.Success(_: Guest) =>
        request.headerMap.add(solverHeader, "")
        service(request).map { response =>
          response.headerMap.remove(solverHeader)
          response
        }

      case zio.Exit.Success(value: Authorized) =>
        request.headerMap.add(solverHeader, value.context.solverId.toString)
        service(request).map { response =>
          response.headerMap.remove(solverHeader)
          response
        }
    }
  }
}

object ContractFilter {
  sealed abstract class TokenError extends Product with Serializable

  final case object SessionNotFound extends TokenError

  final case object FingerprintNotFound extends TokenError

  final case object RemoteNotFound extends TokenError

  final case object SolverNotFound extends TokenError

  def apply(
      assign: (SessionIdValue, FingerprintValue, RemoteValue) => Task[EnvApi],
      enableTrace: Boolean = false
  )(
      implicit runtime: zio.Runtime[zio.ZEnv],
      log: Logger
  ): ContractFilter =
    new ContractFilter(assign, enableTrace)
}
