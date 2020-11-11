package bookingtour.core.finch

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.util.UUID

import bookingtour.protocols.core.sessions._
import bookingtour.protocols.parties.api.sessions.SessionContractAPI
import cats.effect.IO
import cats.instances.all._
import cats.syntax.option._
import cats.syntax.order._
import com.twitter.finagle.http.{Cookie, Status}
import com.twitter.util.Try
import io.estatico.newtype.ops._
import io.finch._
import shapeless.{::, HNil}

/**
  * © Alexey Toroshchin 2019.
  */
object ActiveEndpoints {
  final val OK: Output[Nothing] = Output.empty(Status.Ok)

  final object decoders {
    implicit val decodeLocalDate: DecodePath[LocalDate] =
      DecodePath.instance(s => Try(LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE)).toOption)

    implicit val decodeLocalDateTime: DecodePath[LocalDateTime] =
      DecodePath.instance(s => Try(LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME)).toOption)
  }

  final object instances {
    val cookieKey: String         = "XSRF-TOKEN"
    val sessionHeader: String     = "x-session-id"
    val fingerprintHeader: String = "x-request-id"
    val solverHeader: String      = "x-solver-id"

    def ofCookie(sessionId: SessionIdValue, solverId: SolverIdValue): Cookie =
      new Cookie(name = cookieKey, value = s"${sessionId.x.toString}++${solverId.x.toString}")

    /*def parseCookieValue(x: String): Option[(SessionIdValue, SolverIdValue)] = {
      for {
        value <- Option(x).filterNot(_.isEmpty)
        xs = value.split("\\+\\+").toList.flatMap(a => Try(UUID.fromString(a)).toOption.toList)
        b <- if (xs.size =!= 2) {
          none[SessionIdValue]
        } else {
          (xs.head.coerce[SessionIdValue], xs.last.coerce[SolverIdValue])
        }
      } yield b
    }*/

    def apiVersion(ver: String): Endpoint[IO, HNil] = new Endpoint[IO, HNil] {
      private[this] val apiVersionScheme =
        "application/vnd.d0c.api.v([1-9][0-9]?)\\+application/json".r
      override def apply(input: Input): EndpointResult[IO, HNil] = {
        input.request.headerMap.get("Content-Type") match {
          case Some(apiVersionScheme(version)) if ver === version =>
            EndpointResult.Matched[IO, HNil](input, Trace.empty, IO(Output.empty(Status.Ok)))
          case _ =>
            EndpointResult.NotMatched[IO]
        }
      }
    }

    private def sessionFingerprint: Endpoint[IO, FingerprintValue] = {
      io.finch.catsEffect
        .header(fingerprintHeader)
        .mapOutput { x =>
          if (Option(x).isEmpty || x.isEmpty) {
            Output.failure(new Exception("Fingerprint not exist"), Status.EnhanceYourCalm)
          } else
            Ok(x.coerce[FingerprintValue])
        }
    }

    private def sessionId: Endpoint[IO, SessionIdValue] = {
      io.finch.catsEffect
        .header(sessionHeader)
        .mapOutput { x =>
          if (Option(x).isEmpty || x.isEmpty) {
            Output.failure(new Exception("Session id not exist"), Status.EnhanceYourCalm)
          } else
            Ok(UUID.fromString(x).coerce[SessionIdValue])
        }
    }

    private def solverId: Endpoint[IO, Option[SolverIdValue]] = {
      io.finch.catsEffect
        .header(solverHeader)
        .mapOutput { x =>
          if (Option(x).isEmpty || x.isEmpty) {
            Ok(none)
          } else
            Ok(UUID.fromString(x).coerce[SolverIdValue].some)
        }
    }

    /*private def cookieEndpoint: Endpoint[IO, Option[(SessionIdValue, SolverIdValue)]] =
      io.finch.catsEffect.cookie(cookieKey).map(x => parseCookieValue(x.value))*/

    private def sessionIp: Endpoint[IO, RemoteValue] =
      io.finch.catsEffect.root.map(_.remoteAddress.getHostAddress.coerce[RemoteValue])

    /*def sessionEndpoint: Endpoint[IO, SessionCookieS] =
      (cookieEndpoint :: sessionFingerprint :: sessionIp).map {
        case cookie :: fingerprint :: ip :: HNil =>
          SessionCookieS(
            sessionId = cookie.map(_._1).getOrElse(UUID.randomUUID()),
            solverId = cookie.map(_._2).getOrElse(UUID.randomUUID()),
            fingerprint = fingerprint,
            ip = ip
          )
      }*/
    def contractEndpoint: Endpoint[IO, SessionContractAPI] =
      (sessionId :: sessionFingerprint :: sessionIp :: solverId).map {
        case sessionId :: fingerprint :: ip :: solverOpt :: HNil =>
          SessionContractAPI(
            id = sessionId,
            fingerprint = fingerprint,
            ip = ip,
            solver = solverOpt
          )
      }
  }
}
