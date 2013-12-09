package com.whisk.blackpepper.test

import org.specs2.mutable._
import com.whisk.blackpepper.Implicits._

class QuerySpec extends Specification {

  import Recipes.authorFmt

  "Blackpepper query builder" should {
    "build update queries" in {
      //Primitive
      Recipes.update.where(_.url eqs "url").modify(_.description setTo Some("desc")).qb.toString must_==
        "UPDATE recipes SET description='desc' WHERE url='url';"

      //Set
      Recipes.update.where(_.url eqs "url").modify(_.tags add "tag1").qb.toString must_==
        "UPDATE recipes SET tags=tags+{'tag1'} WHERE url='url';"

      Recipes.update.where(_.url eqs "url").modify(_.tags addAll Seq("tag1", "tag2")).qb.toString must_==
        "UPDATE recipes SET tags=tags+{'tag1','tag2'} WHERE url='url';"

      //Json
      Recipes.update.where(_.url eqs "url").modify(_.author setTo Author("fname", "lname", None)).qb.toString must_==
        """UPDATE recipes SET author='{"firstName":"fname","lastName":"lname"}' WHERE url='url';"""

      //Seq
      Recipes.update.where(_.url eqs "url").modify(_.ingredients append "ingr1").qb.toString must_==
        "UPDATE recipes SET ingredients=ingredients+['ingr1'] WHERE url='url';"

      Recipes.update.where(_.url eqs "url").modify(_.ingredients prepend "ingr1").qb.toString must_==
        "UPDATE recipes SET ingredients=['ingr1']+ingredients WHERE url='url';"

      //Map
      Recipes.update.where(_.url eqs "url").modify(_.props put ("k1", "v1")).qb.toString must_==
        "UPDATE recipes SET props['k1']='v1' WHERE url='url';"

      Recipes.update.where(_.url eqs "url").modify(_.props putAll Map("k1" -> "v1", "k2" -> "v2")).qb.toString must_==
        "UPDATE recipes SET props=props+{'k1':'v1','k2':'v2'} WHERE url='url';"
    }
  }
}
