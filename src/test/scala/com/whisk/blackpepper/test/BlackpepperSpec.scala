package com.whisk.blackpepper.test

import com.whisk.blackpepper.Implicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.datastax.driver.core.Session
import scala.concurrent.{ Await, Future }
import com.datastax.driver.core.utils.UUIDs

class BlackpepperSpec extends CassandraSpec {

  implicit class SyncFuture[T](future: Future[T]) {
    def sync(): T = {
      Await.result(future, Duration(5, "seconds"))
    }
  }

  implicit val _session: Session = session

  "Blackpepper DSL" should {

    val author = Author("Tony", "Clark", Some("great chef..."))
    val r = Recipe("recipe_url", Some("desc"), Seq("ingr1", "ingr2"), Some(author), Some(4), new java.util.Date, Map("a" -> "b", "c" -> "d"), Set("tag1", "tag2"))

    "support inserting, updating and deleting rows" in {
      Recipes.insert
        .value(_.url, r.url)
        .valueOrNull(_.description, r.description)
        .value(_.ingredients, r.ingredients)
        .valueOrNull(_.author, r.author)
        .valueOrNull(_.servings, r.servings)
        .value(_.lastCheckedAt, r.lastCheckedAt)
        .value(_.props, r.props)
        .value(_.uid, UUIDs.timeBased())
        .value(_.tags, r.tags)
        .execute().sync()

      val recipeF: Future[Option[Recipe]] = Recipes.select.one
      recipeF.sync() should beSome(r)

      Recipes.select.fetch.sync() should contain(r)

      Recipes.update.where(_.url eqs r.url).modify(_.description setTo Some("new desc")).and(_.tags add "tag3").execute().sync()

      Recipes.select(_.description).where(_.url eqs r.url).one.map(_.flatten).sync() should beSome("new desc")

      Recipes.delete.where(_.url eqs r.url).execute().sync()
      Recipes.select.fetch.sync() should beEmpty
    }

  }
}
