package com.whisk.blackpepper

import com.datastax.driver.core.Session

abstract class CTable[T <: CTable[T, R], R](val tableName: String) extends QueryTable[T, R, Session] with ColumnDefinitions