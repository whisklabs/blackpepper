package com.whisk.blackpepper

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder._
import play.api.libs.json.Format
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object CTableMacros {

  def macroCol[T](c: Context)(implicit e1: c.WeakTypeTag[T]): c.Expr[PrimitiveColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]

    val field: String = c.enclosingClass.collect({
      case ValDef(_, name, _, rhs) if rhs.pos == c.macroApplication.pos => name.decodedName.toString
    }).headOption.getOrElse(c.abort(c.enclosingPosition, "invalid definition"))

    c.Expr[PrimitiveColumn[T]](q"""new com.whisk.blackpepper.PrimitiveColumn[$tpe]($field)""")
  }
}

abstract class CTable[T <: CTable[T, R], R](val tableName: String) {

  def fromRow(r: Row): R

  def column[RR]: PrimitiveColumn[RR] = macro CTableMacros.macroCol[RR]

  def column[RR: CSPrimitive](name: String): PrimitiveColumn[RR] =
    new PrimitiveColumn[RR](name)

  def optColumn[RR: CSPrimitive](name: String): OptionalPrimitiveColumn[RR] =
    new OptionalPrimitiveColumn[RR](name)

  def jsonColumn[RR: Format](name: String): JsonColumn[RR] =
    new JsonColumn[RR](name)

  def optionalJsonColumn[RR: Format](name: String): OptionalJsonColumn[RR] =
    new OptionalJsonColumn[RR](name)

  def enumColumn[EnumType <: Enumeration](enum: EnumType, name: String): EnumColumn[EnumType] =
    new EnumColumn[EnumType](enum, name)

  def seqColumn[RR: CSPrimitive](name: String): SeqColumn[RR] =
    new SeqColumn[RR](name)

  def setColumn[RR: CSPrimitive](name: String): SetColumn[RR] =
    new SetColumn[RR](name)

  def mapColumn[K: CSPrimitive, V: CSPrimitive](name: String) =
    new MapColumn[K, V](name)

  def jsonSeqColumn[RR: Format](name: String): JsonSeqColumn[RR] =
    new JsonSeqColumn[RR](name)

  def jsonSetColumn[RR: Format](name: String): JsonSetColumn[RR] =
    new JsonSetColumn[RR](name)

  def jsonMapColumn[K: CSPrimitive, V: Format](name: String): JsonMapColumn[K, V] =
    new JsonMapColumn[K, V](name)

  def select: SelectQuery[T, R] =
    new SelectQuery[T, R](this.asInstanceOf[T], QueryBuilder.select().from(tableName), this.asInstanceOf[T].fromRow)

  def select[A](f1: T => SelectColumn[A]): SelectQuery[T, A] = {
    val t = this.asInstanceOf[T]
    val c = f1(t)
    new SelectQuery[T, A](t, QueryBuilder.select(c.col.name).from(tableName), c.apply)
  }

  def select[A, B](f1: T => SelectColumn[A], f2: T => SelectColumn[B]): SelectQuery[T, (A, B)] = {
    val t = this.asInstanceOf[T]
    val c1 = f1(t)
    val c2 = f2(t)
    new SelectQuery[T, (A, B)](t, QueryBuilder.select(c1.col.name, c2.col.name).from(tableName), r => (c1(r), c2(r)))
  }

  def update = new UpdateQuery[T, R](this.asInstanceOf[T], QueryBuilder.update(tableName))

  def insert = new InsertQuery[T, R](this.asInstanceOf[T], QueryBuilder.insertInto(tableName))

  def delete = new DeleteQuery[T, R](this.asInstanceOf[T], QueryBuilder.delete.from(tableName))

}

