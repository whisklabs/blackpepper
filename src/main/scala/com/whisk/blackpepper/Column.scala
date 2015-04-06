package com.whisk.blackpepper

import com.datastax.driver.core.Row
import java.util.{ List => JavaList, Set => JavaSet, Map => JavaMap }
import play.api.libs.json.{ Json, Format }
import scala.collection.JavaConverters._
import scala.util.Try

trait AbstractColumn[T] extends CSWrites[T] {

  type ValueType

  def name: String

  def apply(r: Row): ValueType

  def optional(r: Row): Option[T]
}

trait Column[T] extends AbstractColumn[T] {

  type ValueType = T

  override def apply(r: Row): T =
    optional(r).getOrElse(throw new Exception(s"can't extract required value for column '$name'"))
}

trait OptionalColumn[T] extends AbstractColumn[T] {

  type ValueType = Option[T]

  override def apply(r: Row) = optional(r)
}

class OptionalPrimitiveColumn[T: CSPrimitive](val name: String) extends OptionalColumn[T] {

  def toCType(v: T): AnyRef = CSPrimitive[T].toCType(v)

  def optional(r: Row): Option[T] = implicitly[CSPrimitive[T]].fromRow(r, name)
}

class PrimitiveColumn[RR: CSPrimitive](val name: String) extends Column[RR] {

  def toCType(v: RR): AnyRef = CSPrimitive[RR].toCType(v)

  def optional(r: Row): Option[RR] =
    implicitly[CSPrimitive[RR]].fromRow(r, name)
}

final class CounterColumn(override val name: String) extends PrimitiveColumn[Long](name)

class EnumColumn[EnumType <: Enumeration](enum: EnumType, val name: String) extends Column[EnumType#Value] {

  def toCType(v: EnumType#Value): AnyRef = v.toString

  def optional(r: Row): Option[EnumType#Value] = {
    if (r.isNull(name)) None else Some(r.getString(name)).flatMap(s => enum.values.find(_.toString == s))
  }

}

trait CollectionValueDefinition[RR] {

  def valueCls: Class[_]
  def valueToCType(v: RR): AnyRef
  def valueFromCType(c: AnyRef): RR
}

trait PrimitiveCollecitonValue[R] extends CollectionValueDefinition[R] {

  def valuePrimitive: CSPrimitive[R]

  override def valueCls = valuePrimitive.cls

  override def valueToCType(v: R): AnyRef = valuePrimitive.toCType(v)

  override def valueFromCType(c: AnyRef): R = valuePrimitive.fromCType(c)

}

abstract class AbstractSeqColumn[RR] extends Column[Seq[RR]] with CollectionValueDefinition[RR] {

  def valuesToCType(values: Iterable[RR]): JavaList[AnyRef] =
    values.map(valueToCType).toList.asJava

  override def toCType(values: Seq[RR]): AnyRef = valuesToCType(values)

  override def apply(r: Row): Seq[RR] = {
    optional(r).getOrElse(Nil)
  }

  override def optional(r: Row): Option[Seq[RR]] = {
    if (r.isNull(name)) None
    else Try {
      r.getList(name, valueCls).asScala.map(e => valueFromCType(e.asInstanceOf[AnyRef])).toList
    }.toOption
  }
}

abstract class AbstractSetColumn[RR] extends Column[Set[RR]] with CollectionValueDefinition[RR] {

  def valuesToCType(values: Iterable[RR]): JavaSet[AnyRef] =
    values.map(valueToCType).toSet.asJava

  override def toCType(values: Set[RR]): AnyRef = valuesToCType(values)

  override def apply(r: Row): Set[RR] = {
    optional(r).getOrElse(Set.empty[RR])
  }

  override def optional(r: Row): Option[Set[RR]] = {
    if (r.isNull(name)) None
    else Try {
      r.getSet(name, valueCls).asScala.map(e => valueFromCType(e.asInstanceOf[AnyRef])).toSet
    }.toOption
  }
}

abstract class AbstractMapColumn[K, V] extends Column[Map[K, V]] with CollectionValueDefinition[V] {

  def keyCls: Class[_]

  def keyToCType(v: K): AnyRef

  def keyFromCType(c: AnyRef): K

  def valuesToCType(values: Traversable[(K, V)]): JavaMap[AnyRef, AnyRef] =
    values.map({ case (k, v) => keyToCType(k) -> valueToCType(v) }).toMap.asJava

  override def toCType(values: Map[K, V]): AnyRef = valuesToCType(values)

  override def apply(r: Row): Map[K, V] = {
    optional(r).getOrElse(Map.empty[K, V])
  }

  def optional(r: Row): Option[Map[K, V]] = {
    if (r.isNull(name)) None
    else Option(r.getMap(name, keyCls, valueCls)).map(_.asScala.map {
      case (k, v) =>
        keyFromCType(k.asInstanceOf[AnyRef]) -> valueFromCType(v.asInstanceOf[AnyRef])
    }.toMap)
  }
}

class SeqColumn[RR: CSPrimitive](val name: String) extends AbstractSeqColumn[RR] with PrimitiveCollecitonValue[RR] {

  override val valuePrimitive = CSPrimitive[RR]
}

class SetColumn[RR: CSPrimitive](val name: String) extends AbstractSetColumn[RR] with PrimitiveCollecitonValue[RR] {

  override val valuePrimitive = CSPrimitive[RR]
}

class MapColumn[K: CSPrimitive, V: CSPrimitive](val name: String) extends AbstractMapColumn[K, V] with PrimitiveCollecitonValue[V] {

  val keyPrimitive = CSPrimitive[K]

  override def keyCls: Class[_] = keyPrimitive.cls

  override def keyToCType(v: K): AnyRef = keyPrimitive.toCType(v)

  override def keyFromCType(c: AnyRef): K = keyPrimitive.fromCType(c)

  override val valuePrimitive = CSPrimitive[V]
}
