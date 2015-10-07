package com.whisk.blackpepper

import play.api.libs.json.Format

trait ColumnDefinitions {

  def column[RR: CSPrimitive](name: String): PrimitiveColumn[RR] =
    new PrimitiveColumn[RR](name)

  def optColumn[RR: CSPrimitive](name: String): OptionalPrimitiveColumn[RR] =
    new OptionalPrimitiveColumn[RR](name)

  def counterColumn(name: String): CounterColumn = new CounterColumn(name)

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
}
