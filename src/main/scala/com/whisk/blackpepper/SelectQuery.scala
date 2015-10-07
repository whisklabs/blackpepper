package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder.{ Clause, Select }
import com.datastax.driver.core.Row

class SelectQuery[T <: QueryTable[T, _, SessionType], R, SessionType: SessionProvider](val table: T, val qb: Select, rowFunc: Row => R) extends ExecutableQuery[T, R, SessionType] {

  override def fromRow(r: Row): R = rowFunc(r)

  def where(c: T => Clause): SelectWhere[T, R, SessionType] = {
    new SelectWhere[T, R, SessionType](table, qb.where(c(table)), fromRow)
  }

  def limit(l: Int): SelectQuery[T, R, SessionType] = {
    new SelectQuery(table, qb.limit(l), fromRow)
  }
}

class SelectWhere[T <: QueryTable[T, _, SessionType], R, SessionType: SessionProvider](val table: T, val qb: Select.Where, rowFunc: Row => R) extends ExecutableQuery[T, R, SessionType] {

  override def fromRow(r: Row): R = rowFunc(r)

  def where(c: T => Clause): SelectWhere[T, R, SessionType] = {
    new SelectWhere[T, R, SessionType](table, qb.and(c(table)), fromRow)
  }

  def limit(l: Int): SelectQuery[T, R, SessionType] = {
    new SelectQuery(table, qb.limit(l), fromRow)
  }

  def and = where _

}