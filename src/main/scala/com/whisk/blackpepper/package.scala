package com.whisk

import com.datastax.driver.core.Session

package object blackpepper {

  implicit object IdentitySessionProvider extends SessionProvider[Session] {
    override def session(t: Session): Session = t
  }
}
