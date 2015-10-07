package com.whisk.blackpepper

abstract class KeyspaceTable[T <: KeyspaceTable[T, R, SessionType], R, SessionType <: KeyspaceSession](val tableName: String)
  extends QueryTable[T, R, SessionType] with ColumnDefinitions
