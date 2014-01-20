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

class SeqLikeModifyColumn[RR: CSPrimitive](col: SeqColumn[RR]) extends ModifyColumn[Seq[RR]](col) {

  def prepend(value: RR): Assignment = QueryBuilder.prepend(col.name, CSPrimitive[RR].toCType(value))
  def prependAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.prependAll(col.name, values.map(CSPrimitive[RR].toCType).toList.asJava)
  def append(value: RR): Assignment = QueryBuilder.append(col.name, CSPrimitive[RR].toCType(value))
  def appendAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.appendAll(col.name, values.map(CSPrimitive[RR].toCType).toList.asJava)
  def remove(value: RR): Assignment = QueryBuilder.remove(col.name, CSPrimitive[RR].toCType(value))
  def removeAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.removeAll(col.name, values.map(CSPrimitive[RR].toCType).toSet.asJava)
}

class SetLikeModifyColumn[RR: CSPrimitive](col: SetColumn[RR]) extends ModifyColumn[Set[RR]](col) {

  def add(value: RR): Assignment = QueryBuilder.add(col.name, CSPrimitive[RR].toCType(value))
  def addAll[L <% Traversable[RR]](values: L): Assignment = QueryBuilder.addAll(col.name, values.map(CSPrimitive[RR].toCType).toSet.asJava)
  def remove(value: RR): Assignment = QueryBuilder.remove(col.name, CSPrimitive[RR].toCType(value))
  def removeAll[L <% Seq[RR]](values: L): Assignment = QueryBuilder.removeAll(col.name, values.map(CSPrimitive[RR].toCType).toSet.asJava)
}

class MapLikeModifyColumn[A: CSPrimitive, B: CSPrimitive](col: MapColumn[A, B]) extends ModifyColumn[Map[A, B]](col) {

  def put(value: (A, B)): Assignment = QueryBuilder.put(col.name, CSPrimitive[A].toCType(value._1), CSPrimitive[B].toCType(value._2))
  def putAll[L <% Traversable[(A, B)]](values: L): Assignment = {
    val map = values.map({ case (k, v) => CSPrimitive[A].toCType(k) -> CSPrimitive[B].toCType(v) }).toMap.asJava
    QueryBuilder.putAll(col.name, map)
  }
  def remove(key: A): Assignment = QueryBuilder.remove(col.name, CSPrimitive[A].toCType(key))
  def removeAll[L <% Seq[A]](keys: L): Assignment = QueryBuilder.removeAll(col.name, keys.map(CSPrimitive[A].toCType).toSet.asJava)
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