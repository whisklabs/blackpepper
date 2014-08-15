package com.whisk.blackpepper

import com.datastax.driver.core.{ Row, ResultSet, Session, Statement }
import scala.concurrent.{ Future, ExecutionContext }
import scala.collection.JavaConverters._
import play.api.libs.iteratee.{ Iteratee, Enumerator }

trait ExecutableStatement extends CassandraResultSetOperations {

  def qb: Statement

  def execute()(implicit session: Session, ec: ExecutionContext): Future[ResultSet] =
    session.executeAsync(qb)
}

trait ExecutableQuery[T <: CTable[T, _], R] extends CassandraResultSetOperations {

  def qb: Statement
  def table: CTable[T, _]
  def fromRow(r: Row): R

  def execute()(implicit session: Session, ec: ExecutionContext): Future[ResultSet] =
    session.executeAsync(qb)

  def fetchSync(implicit session: Session): Seq[R] = {
    session.execute(qb).all().asScala.toSeq.map(fromRow)
  }

  def fetch(implicit session: Session, ec: ExecutionContext): Future[Seq[R]] = {
    fetchEnumerator(session, ec).flatMap(_.run(Iteratee.getChunks))
  }

  def fetchEnumerator(implicit session: Session, ec: ExecutionContext): Future[Enumerator[R]] = {
    session.executeAsync(qb).map(rs =>
      Enumerator.enumerate(rs.iterator().asScala).map(fromRow))
  }

  def one(implicit session: Session, ec: ExecutionContext): Future[Option[R]] = {
    session.executeAsync(qb).map(r => Option(r.one()).map(fromRow))
  }
}
