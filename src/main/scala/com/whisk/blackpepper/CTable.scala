package com.whisk.blackpepper

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder._
import play.api.libs.json.Format
import scala.language.experimental.macros
import scala.reflect.macros.Context

object CTableMacros {

  def toColumnName(fieldName: String): String = {
    fieldName.replaceAll("""(\p{Ll})(\p{Lu})""", "$1_$2").toLowerCase
  }

  def fieldName[C <: Context](c: C): String = {
    c.enclosingClass.collect({
      case c.universe.ValDef(_, name, _, rhs) if rhs.pos == c.macroApplication.pos => name.decodedName.toString
    }).headOption.getOrElse(c.abort(c.enclosingPosition, "invalid definition"))
  }

  def columnName[C <: Context](c: C): String = {
    toColumnName(fieldName(c))
  }

  def primitiveColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[PrimitiveColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[PrimitiveColumn[T]](q"""new com.whisk.blackpepper.PrimitiveColumn[$tpe]($field)""")
  }

  def optPrimitiveColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[OptionalPrimitiveColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[OptionalPrimitiveColumn[T]](q"""new com.whisk.blackpepper.OptionalPrimitiveColumn[$tpe]($field)""")
  }

  def counterColumnImpl(c: Context): c.Expr[CounterColumn] = {
    import c.universe._
    val field: String = columnName(c)
    c.Expr[CounterColumn](q"""new com.whisk.blackpepper.CounterColumn($field)""")
  }

  def jsonColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[JsonColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[JsonColumn[T]](q"""new com.whisk.blackpepper.JsonColumn[$tpe]($field)""")
  }

  def optJsonColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[OptionalJsonColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[OptionalJsonColumn[T]](q"""new com.whisk.blackpepper.OptionalJsonColumn[$tpe]($field)""")
  }

  def enumColumnImpl[T <: Enumeration: c.WeakTypeTag](c: Context)(enum: c.Expr[T]): c.Expr[EnumColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[EnumColumn[T]](q"""new com.whisk.blackpepper.EnumColumn[$tpe]($enum, $field)""")
  }

  def seqColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[SeqColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[SeqColumn[T]](q"""new com.whisk.blackpepper.SeqColumn[$tpe]($field)""")
  }

  def setColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[SetColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[SetColumn[T]](q"""new com.whisk.blackpepper.SetColumn[$tpe]($field)""")
  }

  def mapColumnImpl[K: c.WeakTypeTag, V: c.WeakTypeTag](c: Context): c.Expr[MapColumn[K, V]] = {
    import c.universe._
    val kTpe = weakTypeOf[K]
    val vTpe = weakTypeOf[V]
    val field: String = columnName(c)
    c.Expr[MapColumn[K, V]](q"""new com.whisk.blackpepper.MapColumn[$kTpe, $vTpe]($field)""")
  }

  def jsonSeqColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[JsonSeqColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[JsonSeqColumn[T]](q"""new com.whisk.blackpepper.JsonSeqColumn[$tpe]($field)""")
  }

  def jsonSetColumnImpl[T: c.WeakTypeTag](c: Context): c.Expr[JsonSetColumn[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val field: String = columnName(c)
    c.Expr[JsonSetColumn[T]](q"""new com.whisk.blackpepper.JsonSetColumn[$tpe]($field)""")
  }

  def jsonMapColumnImpl[K: c.WeakTypeTag, V: c.WeakTypeTag](c: Context): c.Expr[JsonMapColumn[K, V]] = {
    import c.universe._
    val kTpe = weakTypeOf[K]
    val vTpe = weakTypeOf[V]
    val field: String = columnName(c)
    c.Expr[JsonMapColumn[K, V]](q"""new com.whisk.blackpepper.JsonMapColumn[$kTpe, $vTpe]($field)""")
  }
}

abstract class CTable[T <: CTable[T, R], R](val tableName: String) {

  def fromRow(r: Row): R

  def column[RR]: PrimitiveColumn[RR] = macro CTableMacros.primitiveColumnImpl[RR]

  def column[RR: CSPrimitive](name: String): PrimitiveColumn[RR] =
    new PrimitiveColumn[RR](name)

  def optColumn[RR]: OptionalPrimitiveColumn[RR] = macro CTableMacros.optPrimitiveColumnImpl[RR]

  def optColumn[RR: CSPrimitive](name: String): OptionalPrimitiveColumn[RR] =
    new OptionalPrimitiveColumn[RR](name)

  def counterColumn: CounterColumn = macro CTableMacros.counterColumnImpl

  def counterColumn(name: String): CounterColumn = new CounterColumn(name)

  def jsonColumn[RR]: JsonColumn[RR] = macro CTableMacros.jsonColumnImpl[RR]

  def jsonColumn[RR: Format](name: String): JsonColumn[RR] =
    new JsonColumn[RR](name)

  def optionalJsonColumn[RR]: OptionalJsonColumn[RR] = macro CTableMacros.optJsonColumnImpl[RR]

  def optionalJsonColumn[RR: Format](name: String): OptionalJsonColumn[RR] =
    new OptionalJsonColumn[RR](name)

  def enumColumn[EnumType <: Enumeration](enum: EnumType): EnumColumn[EnumType] = macro CTableMacros.enumColumnImpl[EnumType]

  def enumColumn[EnumType <: Enumeration](enum: EnumType, name: String): EnumColumn[EnumType] =
    new EnumColumn[EnumType](enum, name)

  def seqColumn[RR]: SeqColumn[RR] = macro CTableMacros.seqColumnImpl[RR]

  def seqColumn[RR: CSPrimitive](name: String): SeqColumn[RR] =
    new SeqColumn[RR](name)

  def setColumn[RR]: SetColumn[RR] = macro CTableMacros.setColumnImpl[RR]

  def setColumn[RR: CSPrimitive](name: String): SetColumn[RR] =
    new SetColumn[RR](name)

  def mapColumn[K, V]: MapColumn[K, V] = macro CTableMacros.mapColumnImpl[K, V]

  def mapColumn[K: CSPrimitive, V: CSPrimitive](name: String) =
    new MapColumn[K, V](name)

  def jsonSeqColumn[RR]: JsonSeqColumn[RR] = macro CTableMacros.jsonSeqColumnImpl[RR]

  def jsonSeqColumn[RR: Format](name: String): JsonSeqColumn[RR] =
    new JsonSeqColumn[RR](name)

  def jsonSetColumn[RR]: JsonSetColumn[RR] = macro CTableMacros.jsonSetColumnImpl[RR]

  def jsonSetColumn[RR: Format](name: String): JsonSetColumn[RR] =
    new JsonSetColumn[RR](name)

  def jsonMapColumn[K, V]: JsonMapColumn[K, V] = macro CTableMacros.jsonMapColumnImpl[K, V]

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

