package com.whisk.blackpepper.test

import com.whisk.blackpepper.CTable
import play.api.libs.json.Json
import com.datastax.driver.core.Row
import java.util.UUID

object Difficulty extends Enumeration {

  val Easy = Value
}

case class Author(firstName: String, lastName: String, bio: Option[String])

case class Recipe(
  url: String,
  description: Option[String],
  ingredients: Seq[String],
  author: Option[Author],
  servings: Option[Int],
  lastCheckedAt: java.util.Date,
  props: Map[String, String],
  tags: Set[String])

class Recipes extends CTable[Recipes, Recipe]("recipes") {

  implicit val authorFmt = Json.format[Author]

  override def fromRow(r: Row): Recipe = {
    Recipe(url(r), description(r), ingredients(r), author.optional(r), servings(r), lastCheckedAt(r), props(r), tags(r))
  }

  val url = column[String]("url")
  val description = optColumn[String]("description")
  val ingredients = seqColumn[String]("ingredients")
  val author = jsonColumn[Author]("author")
  val servings = optColumn[Int]("servings")
  val lastCheckedAt = column[java.util.Date]("last_checked_at")
  val props = mapColumn[String, String]("props")
  val uid = column[UUID]("uid")
  val tags = setColumn[String]("tags")
  val difficulty = enumColumn(Difficulty, "difficulty")
}

object Recipes extends Recipes