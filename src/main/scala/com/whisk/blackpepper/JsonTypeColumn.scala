package com.whisk.blackpepper

import play.api.libs.json.{ Json, Format }
import com.datastax.driver.core.Row

trait JsonColumnBase {

  def valueToString[T: Format](v: T): String =
    Json.stringify(Json.toJson(v))

  def fromString[T: Format](str: String): Option[T] = {
    Json.fromJson(Json.parse(str)).asOpt
  }

  val primitive = implicitly[CSPrimitive[String]]
}

trait CollectionJsonColumnDefinition[ValueType] extends JsonColumnBase with CollectionValueDefinition[ValueType] {

  implicit def inst: Format[ValueType]

  override val valueCls: Class[_] = classOf[java.lang.String]

  override def valueToCType(v: ValueType): AnyRef = valueToString(v)

  override def valueFromCType(c: AnyRef): ValueType = fromString(c.asInstanceOf[String]).get
}

class JsonColumn[RR: Format](val name: String) extends Column[RR] with JsonColumnBase {

  def toCType(v: RR): AnyRef = valueToString(v)

  def optional(r: Row): Option[RR] = {
    fromString(r.getString(name))
  }
}

class OptionalJsonColumn[RR: Format](val name: String) extends OptionalColumn[RR] with JsonColumnBase {

  def toCType(v: RR): AnyRef = {
    valueToString(v).asInstanceOf[AnyRef]
  }

  def optional(r: Row): Option[RR] = {
    fromString(r.getString(name))
  }

}

class JsonSeqColumn[RR: Format](val name: String) extends AbstractSeqColumn[RR] with CollectionJsonColumnDefinition[RR] {

  override val inst = implicitly[Format[RR]]
}

class JsonSetColumn[RR: Format](val name: String) extends AbstractSetColumn[RR] with CollectionJsonColumnDefinition[RR] {

  override val inst = implicitly[Format[RR]]
}

class JsonMapColumn[K: CSPrimitive, V: Format](val name: String) extends AbstractMapColumn[K, V]
    with CollectionJsonColumnDefinition[V] {

  override val inst = implicitly[Format[V]]

  val keyPrimitive = CSPrimitive[K]

  override def keyCls: Class[_] = keyPrimitive.cls

  override def keyToCType(v: K): AnyRef = keyPrimitive.toCType(v)

  override def keyFromCType(c: AnyRef): K = keyPrimitive.fromCType(c)
}