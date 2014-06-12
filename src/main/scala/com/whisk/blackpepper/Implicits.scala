package com.whisk.blackpepper

import play.api.libs.json.Format

object Implicits {

  implicit def columnToQueryColumn[RR: CSPrimitive](col: Column[RR]) =
    new QueryColumn(col)

  implicit def simpleColumnToAssignment[RR: CSPrimitive](col: AbstractColumn[RR]) = {
    new ModifyColumn[RR](col)
  }

  implicit def counterColumnToAssignmentcol(col: CounterColumn) = {
    new ModifyCounterColumn(col)
  }

  implicit def simpleOptionalColumnToAssignment[RR: CSPrimitive](col: OptionalColumn[RR]) = {
    new ModifyColumnOptional[RR](col)
  }

  implicit def enumColumnToAssignment[RR <: Enumeration](col: EnumColumn[RR]) = {
    new ModifyColumn[RR#Value](col)
  }

  implicit def jsonColumnToAssignment[RR: Format](col: JsonColumn[RR]) = {
    new ModifyColumn[RR](col)
  }

  implicit def seqColumnToAssignment[RR](col: AbstractSeqColumn[RR]) = {
    new SeqLikeModifyColumn[RR](col)
  }

  implicit def setColumnToAssignment[RR](col: AbstractSetColumn[RR]) = {
    new SetLikeModifyColumn[RR](col)
  }

  implicit def mapColumnToAssignment[A, B](col: AbstractMapColumn[A, B]) = {
    new MapLikeModifyColumn[A, B](col)
  }

  implicit def columnIsSelectable[T](col: Column[T]): SelectColumn[T] =
    new SelectColumnRequired[T](col)

  implicit def optionalColumnIsSelectable[T](col: OptionalColumn[T]): SelectColumn[Option[T]] =
    new SelectColumnOptional[T](col)
}
