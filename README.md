# blackpepper

Blackpepper is a type-safe Scala DSL for dealing with Cassandra

    Recipes.select(_.description, _.author).where(_.url eqs r.url).one
