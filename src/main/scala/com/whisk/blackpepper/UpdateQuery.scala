package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder._

class UpdateQuery[T <: CTable[T, R], R](table: T, val qb: Update) {

  def where(c: T => Clause): UpdateWhere[T, R] = {
    new UpdateWhere[T, R](table, qb.where(c(table)))
  }

}

class UpdateWhere[T <: CTable[T, R], R](table: T, val qb: Update.Where) {

  def where(c: T => Clause): UpdateWhere[T, R] = {
    new UpdateWhere[T, R](table, qb.and(c(table)))
  }

  def and = where _

  def modify(a: T => Assignment): AssignmentsQuery[T, R] = {
    new AssignmentsQuery[T, R](table, qb.`with`(a(table)))
  }

}

class AssignmentsQuery[T <: CTable[T, R], R](table: T, val qb: Update.Assignments) extends ExecutableStatement {

  def usingTtl(seconds: Int): AssignmentOptionQuery[T, R] = {
    new AssignmentOptionQuery[T, R](table, qb.using(QueryBuilder.ttl(seconds)))
  }

  def using(u: Using): AssignmentOptionQuery[T, R] = {
    new AssignmentOptionQuery[T, R](table, qb.using(u))
  }

  def modify(a: T => Assignment): AssignmentsQuery[T, R] = {
    new AssignmentsQuery[T, R](table, qb.and(a(table)))
  }

  def and = modify _

}

class AssignmentOptionQuery[T <: CTable[T, R], R](table: T, val qb: Update.Options) extends ExecutableStatement {

  def usingTtl(seconds: Int): AssignmentOptionQuery[T, R] = {
    new AssignmentOptionQuery[T, R](table, qb.and(QueryBuilder.ttl(seconds)))
  }

  def using(u: Using): AssignmentOptionQuery[T, R] = {
    new AssignmentOptionQuery[T, R](table, qb.and(u))
  }
}