package com.whisk.blackpepper

import com.datastax.driver.core.{Session, Row}
import com.datastax.driver.core.querybuilder.QueryBuilder

trait SessionProvider[T] {

  def session(t: T): Session
}

abstract class QueryTable[T <: QueryTable[T, RecordType, SessionType], RecordType, SessionType: SessionProvider] {

  def fromRow(r: Row): RecordType
  def tableName: String

  def select: SelectQuery[T, RecordType, SessionType] =
    new SelectQuery[T, RecordType, SessionType](this.asInstanceOf[T], QueryBuilder.select().from(tableName), this.asInstanceOf[T].fromRow)

  def select[A](f1: T => SelectColumn[A]): SelectQuery[T, A, SessionType] = {
    val t = this.asInstanceOf[T]
    val c = f1(t)
    new SelectQuery[T, A, SessionType](t, QueryBuilder.select(c.col.name).from(tableName), c.apply)
  }

  def select[A, B](f1: T => SelectColumn[A], f2: T => SelectColumn[B]): SelectQuery[T, (A, B), SessionType] = {
    val t = this.asInstanceOf[T]
    val c1 = f1(t)
    val c2 = f2(t)
    new SelectQuery[T, (A, B), SessionType](t, QueryBuilder.select(c1.col.name, c2.col.name).from(tableName), r => (c1(r), c2(r)))
  }

  def update = new UpdateQuery[T, RecordType, SessionType](this.asInstanceOf[T], QueryBuilder.update(tableName))

  def insert = new InsertQuery[T, RecordType, SessionType](this.asInstanceOf[T], QueryBuilder.insertInto(tableName))

  def delete = new DeleteQuery[T, RecordType, SessionType](this.asInstanceOf[T], QueryBuilder.delete.from(tableName))
}
