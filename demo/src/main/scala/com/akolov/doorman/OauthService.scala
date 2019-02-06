package com.akolov.doorman

import cats._
import cats.effect._
import com.akolov.doorman.core._
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl


class OauthService[F[_] : Effect : Monad, User](config: DoormanConfig,
                                                clientResource: Resource[F, Client[F]],
                                                val doormanClient: Doorman[F, User],
                                                sessionManager: SessionManager[F, User]
                                               ) extends Http4sDsl[F] {

  object CodeMatcher extends QueryParamDecoderMatcher[String]("code")

  val oauth = new OauthMethods[F, User](clientResource, sessionManager, config)

  def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "login" / configname =>
      oauth.login(configname)

    case GET -> Root / "oauth" / "login" / configname :? CodeMatcher(code) =>
      oauth.callback(configname, code)

  }


}
