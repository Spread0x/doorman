package com.akolov.doorman.demo

import cats.data.NonEmptyList
import cats.effect.{Blocker, ContextShift, Effect}
import cats.implicits._
import com.akolov.doorman.core.{DoormanAuthMiddleware, UserManager, UserTrackingMiddleware}
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import org.http4s.CacheDirective._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Cache-Control`, Location}
import org.http4s.{AuthedRoutes, HttpRoutes, StaticFile, Uri}

class DemoService[F[_]: Effect: ContextShift](userManager: UserManager[F, AppUser])(
  implicit blocker: Blocker
) extends Http4sDsl[F] {

  val track = UserTrackingMiddleware(userManager)
  val auth = DoormanAuthMiddleware(userManager)

  implicit val appUserEncoder: Encoder[AppUser] = deriveEncoder[AppUser]

  val routes = track(
    HttpRoutes.of[F] {
      case GET -> Root =>
        TemporaryRedirect(Location(Uri.unsafeFromString("/index.html")))
      case GET -> Root / "index.html" =>
        StaticFile.fromResource("/web/index.html", blocker).getOrElseF(NotFound())
    }
  ) <+>
    auth(
      AuthedRoutes.of[AppUser, F] {
        case GET -> Root / "userinfo" as user =>
          Ok(user.asJson, `Cache-Control`(NonEmptyList(`no-cache`(), List(`no-store`, `must-revalidate`))))
      }
    )

}
