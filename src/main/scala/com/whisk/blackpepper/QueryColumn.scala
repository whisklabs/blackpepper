package com.whisk.blackpepper

import com.datastax.driver.core.querybuilder.{ Assignment, QueryBuilder, Clause }
import play.api.libs.json.{ Json, Format }
import scala.collection.JavaConverters._
import com.datastax.driver.core.Row

abstract class AbstractQueryColumn[RR: CSPrimitive](col: Column[RR]) {

  def eqs(value: RR): Clause = QueryBuilder.eq(col.name, CSPrimitive[RR].toCType(value))

  def in[L <% Traversable[RR]](vs: L) = QueryBuilder.in(col.name, vs.map(CSPrimitive[RR].toCType).toSeq: _*)

  def gt(value: RR): Clause = QueryBuilder.gt(col.name, CSPrimitive[RR].toCType(value))
  def gte(value: RR): Clause = QueryBuilder.gte(col.name, CSPrimitive[RR].toCType(value))
  def lt(value: RR): Clause = QueryBuilder.lt(col.name, CSPrimitive[RR].toCType(value))
  def lte(value: RR): Clause = QueryBuilder.lte(col.name, CSPrimitive[RR].toCType(value))
}

class QueryColumn[RR: CSPrimitive](col: Column[RR]) extends AbstractQueryColumn[RR](col)

abstract class AbstractModifyColumn[RR](name: String) {

  def toCType(v: RR): AnyRef

  def setTo(value: RR): Assignment = QueryBuilder.set(name, toCType(value))
}

class ModifyColumn[RR](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col.name) {

  def toCType(v: RR): AnyRef = col.toCType(v)
}

class SeqLikeModifyColumn[RR](col: AbstractSeqColumn[RR]) extends ModifyColumn[Seq[RR]](col) {

  def prepend(value: RR): Assignment = QueryBuilder.prepend(col.name, col.valueToCType(value))
  def prependAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.prependAll(col.name, col.valuesToCType(values))
  def append(value: RR): Assignment = QueryBuilder.append(col.name, col.valueToCType(value))
  def appendAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.appendAll(col.name, col.valuesToCType(values))
  def discard(value: RR): Assignment = QueryBuilder.discard(col.name, col.valueToCType(value))
  def discardAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.discardAll(col.name, col.valuesToCType(values))
  def setIdx(i: Int, value: RR): Assignment = QueryBuilder.setIdx(col.name, i, col.valueToCType(value))
}

class SetLikeModifyColumn[RR](col: AbstractSetColumn[RR]) extends ModifyColumn[Set[RR]](col) {

  def add(value: RR): Assignment = QueryBuilder.add(col.name, col.valueToCType(value))
  def addAll(values: Set[RR]): Assignment = QueryBuilder.addAll(col.name, col.valuesToCType(values))
  def remove(value: RR): Assignment = QueryBuilder.remove(col.name, col.valueToCType(value))
  def removeAll(values: Set[RR]): Assignment = QueryBuilder.removeAll(col.name, col.valuesToCType(values))
}

class MapLikeModifyColumn[A, B](col: AbstractMapColumn[A, B]) extends ModifyColumn[Map[A, B]](col) {

  def put(value: (A, B)): Assignment = QueryBuilder.put(col.name, col.keyToCType(value._1), col.valueToCType(value._2))
  def putAll[L <% Traversable[(A, B)]](values: L): Assignment = QueryBuilder.putAll(col.name, col.valuesToCType(values))
}

class ModifyColumnOptional[RR](col: OptionalColumn[RR]) extends AbstractModifyColumn[Option[RR]](col.name) {

  def toCType(v: Option[RR]): AnyRef = v.map(col.toCType).orNull
}

abstract class SelectColumn[T](val col: AbstractColumn[_]) {

  def apply(r: Row): T
}

class SelectColumnRequired[T](override val col: Column[T]) extends SelectColumn[T](col) {

  def apply(r: Row): T = col.apply(r)
}

class SelectColumnOptional[T](override val col: OptionalColumn[T]) extends SelectColumn[Option[T]](col) {

  def apply(r: Row): Option[T] = col.apply(r)

}

//class CSPrimitiveModifyColumn[RR: CSPrimitive](name: String) extends AbstractModifyColumn[RR](name) {
//
//  def toCType(v: RR): AnyRef = implicitly[CSPrimitive[RR]].toCType(v)
//}
//
//class JsonTypeModifyColumn[RR: Format](name: String) extends AbstractModifyColumn[RR](name) {
//
//  def toCType(v: RR): AnyRef = Json.stringify(Json.toJson(v))
//}
//
//abstract class AbstractSeqModifyColumn[RR](name: String) {
//
//  def toCType(v: RR): AnyRef
//
//  def setTo(values: Seq[RR]): Assignment = QueryBuilder.set(name, values.map(toCType).asJava)
//}
//
//class CSPrimitiveSeqModifyColumn[RR: CSPrimitive](name: String) extends AbstractSeqModifyColumn[RR](name) {
//
//  def toCType(v: RR): AnyRef = implicitly[CSPrimitive[RR]].toCType(v)
//}
//
//class JsonTypeSeqModifyColumn[RR: Format](name: String) extends AbstractSeqModifyColumn[RR](name) {
//
//  def toCType(v: RR): AnyRef = Json.stringify(Json.toJson(v))
//}