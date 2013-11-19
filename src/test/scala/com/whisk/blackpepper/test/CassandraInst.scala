package com.whisk.blackpepper.test

import org.apache.cassandra.service.EmbeddedCassandraService
import org.apache.cassandra.io.util.FileUtils

object CassandraInst {

  lazy val service: EmbeddedCassandraService = {
    System.setProperty("cassandra.config", getClass.getClassLoader.getResource("cassandra.yaml").toString)
    System.setProperty("cassandra-foreground", "yes")
    System.setProperty("log4j.defaultInitOverride", "false")
    val f = new java.io.File("tmp")
    if (f.exists()) FileUtils.deleteRecursive(f)
    f.mkdir()
    val s = new EmbeddedCassandraService
    s.start()
    s
  }

}
