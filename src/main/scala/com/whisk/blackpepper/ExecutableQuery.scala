package com.whisk.blackpepper

import com.datastax.driver.core._
import scala.concurrent.{ Future, ExecutionContext }
import scala.collection.JavaConverters._
import play.api.libs.iteratee.{ Iteratee, Enumerator }

abstract class ExecutableStatement[SessionType: SessionProvider] extends CassandraResultSetOperations {

  def qb: Statement

  def execute()(implicit st: SessionType, ec: ExecutionContext): Future[ResultSet] =
    implicitly[SessionProvider[SessionType]].session(st).executeAsync(qb)

  def withConsistencyLevel(cl: ConsistencyLevel): ExecutableStatement[SessionType] = {
    new BasicExecutableStatement[SessionType](qb.setConsistencyLevel(cl))
  }
}

class BasicExecutableStatement[SessionType: SessionProvider](override val qb: Statement) extends ExecutableStatement[SessionType]

abstract class ExecutableQuery[T <: QueryTable[T, _, SessionType], R, SessionType: SessionProvider] extends CassandraResultSetOperations {

  def qb: Statement
  def table: QueryTable[T, _, SessionType]
  def fromRow(r: Row): R

  def withConsistencyLevel(cl: ConsistencyLevel): ExecutableQuery[T, R, SessionType] = {
    def f = fromRow _
    val t = table
    new ExecutableQuery[T, R, SessionType] {
      override def qb: Statement = qb.setConsistencyLevel(cl)

      override def fromRow(r: Row): R = f(r)

      override def table = t
    }
  }

  private def session(st: SessionType): Session = {
    implicitly[SessionProvider[SessionType]].session(st)
  }

  def execute()(implicit st: SessionType, ec: ExecutionContext): Future[ResultSet] =
    session(st).executeAsync(qb)

  def fetchSync(implicit st: SessionType): Seq[R] = {
    session(st).execute(qb).all().asScala.toSeq.map(fromRow)
  }

  def fetch(implicit st: SessionType, ec: ExecutionContext): Future[Seq[R]] = {
    fetchEnumerator(st, ec).flatMap(_.run(Iteratee.getChunks))
  }

  def fetchEnumerator(implicit st: SessionType, ec: ExecutionContext): Future[Enumerator[R]] = {
    session(st).executeAsync(qb).map(rs =>
      Enumerator.enumerate(rs.iterator().asScala).map(fromRow))
  }

  def one(implicit st: SessionType, ec: ExecutionContext): Future[Option[R]] = {
    session(st).executeAsync(qb).map(r => Option(r.one()).map(fromRow))
  }
}
