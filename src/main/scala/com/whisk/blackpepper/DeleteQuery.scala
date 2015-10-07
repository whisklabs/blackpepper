package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder.{ Clause, Delete }

class DeleteQuery[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Delete) extends ExecutableStatement[SessionType] {

  def where(c: T => Clause): DeleteWhere[T, R, SessionType] = {
    new DeleteWhere[T, R, SessionType](table, qb.where(c(table)))
  }

}

class DeleteWhere[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Delete.Where) extends ExecutableStatement[SessionType] {

  def where(c: T => Clause): DeleteWhere[T, R, SessionType] = {
    new DeleteWhere[T, R, SessionType](table, qb.and(c(table)))
  }

  def and(c: T => Clause) = where(c)

}
