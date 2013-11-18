package com.whisk.blackpepper.test

import org.apache.cassandra.service.EmbeddedCassandraService
import org.apache.cassandra.io.util.FileUtils

object CassandraInst {

  lazy val service: EmbeddedCassandraService = {
    System.setProperty("cassandra.config", getClass.getClassLoader.getResource("cassandra.yaml").toString)
    //    System.setProperty("cassandra.config", "file:///Users/viktortnk/whisk/blackpepper/src/test/resources/cassandra.yaml")
    //    println(this.getClass.getClassLoader.getResource("cassandra.yaml").getPath)
    System.setProperty("cassandra-foreground", "yes")
    System.setProperty("log4j.defaultInitOverride", "false")
    FileUtils.deleteRecursive(new java.io.File("tmp"))
    val s = new EmbeddedCassandraService
    s.start()
    s
  }

}
