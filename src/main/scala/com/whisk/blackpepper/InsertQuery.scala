package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder.Insert

class InsertQuery[T <: QueryTable[T, R, SessionType], R, SessionType: SessionProvider](table: T, val qb: Insert) extends ExecutableStatement[SessionType] {

  def value[RR](c: T => AbstractColumn[RR], value: RR): InsertQuery[T, R, SessionType] = {
    val col = c(table)
    new InsertQuery[T, R, SessionType](table, qb.value(col.name, col.toCType(value)))
  }

  def valueOrNull[RR](c: T => AbstractColumn[RR], value: Option[RR]): InsertQuery[T, R, SessionType] = {
    val col = c(table)
    new InsertQuery[T, R, SessionType](table, qb.value(col.name, value.map(col.toCType).orNull))
  }

}
