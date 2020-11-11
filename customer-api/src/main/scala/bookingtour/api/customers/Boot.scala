package bookingtour.api.customers

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.api.customers.config.{ApiEndpointModule, ApiRuntime}
import cats.effect.{ExitCode, IO, IOApp}
import com.twitter.finagle.Http
import com.twitter.util.Await

/**
  * © Alexey Toroshchin 2019.
  */
object Boot extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      a <- IO(args.flatMap { x =>
            x.split("=").toList.map(_.trim) match {
              case "config_path" :: z :: Nil =>
                List(z)
              case _ =>
                List.empty
            }
          }.head)
      c <- IO(ApiRuntime(a))
      e <- IO {
            implicit val a: ApiRuntime = c
            ApiEndpointModule()
          }
      f <- IO(
            Http.server
              .withStreaming(true)
              .serve(s":${c.httpPort}", e.endpoints)
          )
      g <- IO(Await.ready(f)).map(_ => ExitCode.Success)
    } yield g
}
