package com.whisk

import com.datastax.driver.core.Session

package object blackpepper {

  implicit object IdentitySessionProvider extends SessionProvider[Session] {
    override def session(t: Session): Session = t
  }

  implicit def keyspaceSessionIsSessionProvider[T <: KeyspaceSession] = new SessionProvider[T] {
    override def session(t: T) = t.session
  }
}
