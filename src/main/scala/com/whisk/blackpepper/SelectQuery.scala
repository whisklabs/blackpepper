package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder.{ Clause, Select }
import com.datastax.driver.core.Row

class SelectQuery[T <: CTable[T, _], R](val table: T, val qb: Select, rowFunc: Row => R) extends ExecutableQuery[T, R] {

  override def fromRow(r: Row): R = rowFunc(r)

  def where(c: T => Clause): SelectWhere[T, R] = {
    new SelectWhere[T, R](table, qb.where(c(table)), fromRow)
  }

  def limit(l: Int): SelectQuery[T, R] = {
    new SelectQuery(table, qb.limit(l), fromRow)
  }
}

class SelectWhere[T <: CTable[T, _], R](val table: T, val qb: Select.Where, rowFunc: Row => R) extends ExecutableQuery[T, R] {

  override def fromRow(r: Row): R = rowFunc(r)

  def where(c: T => Clause): SelectWhere[T, R] = {
    new SelectWhere[T, R](table, qb.and(c(table)), fromRow)
  }

  def limit(l: Int): SelectQuery[T, R] = {
    new SelectQuery(table, qb.limit(l), fromRow)
  }

  def and = where _

}