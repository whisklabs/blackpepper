# blackpepper

Blackpepper is a type-safe Scala DSL for dealing with Cassandra

    Recipes.select(_.description, _.author).where(_.url eqs r.url).one

Consider [BlackpepperSpec.scala](https://github.com/whiskteam/blackpepper/blob/master/src/test/scala/com/whisk/blackpepper/test/BlackpepperSpec.scala) as an example
