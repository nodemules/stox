package com.nodemules.stox.security

import com.google.firebase.auth.FirebaseToken

data class Credentials(
  val token: FirebaseToken,
  val idToken: FirebaseToken?
) {
  fun isValid() = token == idToken
}