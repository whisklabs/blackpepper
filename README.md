# blackpepper

Blackpepper is a type-safe Scala DSL for dealing with Cassandra

    Recipes.select(_.description, _.author).where(_.url eqs r.url).one

Consider [BlackpepperSpec.scala](https://github.com/whiskteam/blackpepper/blob/master/src/test/scala/com/whisk/blackpepper/test/BlackpepperSpec.scala) and [QuerySpec.scala](https://github.com/whiskteam/blackpepper/blob/master/src/test/scala/com/whisk/blackpepper/test/QuerySpec.scala) as an example


## Dependency

Supported versions of Scala: 2.11, 2.10

    resolvers += "Whisk Releases" at "https://dl.bintray.com/whisk/maven"
    
    libraryDependencies += "com.whisk" %% "blackpepper" % "0.2.1"

    
