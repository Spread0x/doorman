package com.akolov.doorman.core

import cats.Monad

trait DoormanConfig {
  val cookieName: String

  def provider(provider: String): Option[OauthConfig]
}

trait Doorman[F[_], User] {

  /** Create User from Oauth user data */
  def fromProvider(provider: String, data: Map[String, String]): F[Option[User]]

  /** Create a non-authenticated user */
  def create()(implicit ev: Monad[F]): F[User]

  /** Marshall User to a cookie */
  def toCookie(user: User): String

  /** Unmarshall cookie to User */
  def toUser(cookie: String): F[Option[User]]

}
