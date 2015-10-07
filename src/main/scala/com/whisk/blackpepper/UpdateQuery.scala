package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder._

class UpdateQuery[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Update) {

  def where(c: T => Clause): UpdateWhere[T, R, SessionType] = {
    new UpdateWhere[T, R, SessionType](table, qb.where(c(table)))
  }

}

class UpdateWhere[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Update.Where) {

  def where(c: T => Clause): UpdateWhere[T, R, SessionType] = {
    new UpdateWhere[T, R, SessionType](table, qb.and(c(table)))
  }

  def and = where _

  def modify(a: T => Assignment): AssignmentsQuery[T, R, SessionType] = {
    new AssignmentsQuery[T, R, SessionType](table, qb.`with`(a(table)))
  }

}

class AssignmentsQuery[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Update.Assignments) extends ExecutableStatement[SessionType] {

  def usingTtl(seconds: Int): AssignmentOptionQuery[T, R, SessionType] = {
    new AssignmentOptionQuery[T, R, SessionType](table, qb.using(QueryBuilder.ttl(seconds)))
  }

  def using(u: Using): AssignmentOptionQuery[T, R, SessionType] = {
    new AssignmentOptionQuery[T, R, SessionType](table, qb.using(u))
  }

  def modify(a: T => Assignment): AssignmentsQuery[T, R, SessionType] = {
    new AssignmentsQuery[T, R, SessionType](table, qb.and(a(table)))
  }

  def and = modify _

}

class AssignmentOptionQuery[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Update.Options) extends ExecutableStatement[SessionType] {

  def usingTtl(seconds: Int): AssignmentOptionQuery[T, R, SessionType] = {
    new AssignmentOptionQuery[T, R, SessionType](table, qb.and(QueryBuilder.ttl(seconds)))
  }

  def using(u: Using): AssignmentOptionQuery[T, R, SessionType] = {
    new AssignmentOptionQuery[T, R, SessionType](table, qb.and(u))
  }
}