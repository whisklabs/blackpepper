package com.whisk.blackpepper.test

import org.specs2._
import org.specs2.specification.{ Step, Fragments }
import com.datastax.driver.core.Cluster

trait CassandraCluster {

  CassandraInst.service

  val cluster =
    Cluster.builder()
      .addContactPoint("127.0.0.1")
      .withPort(19042)
      .withoutJMXReporting()
      .withoutMetrics()
      .build()

  val session = cluster.connect()
}

trait CassandraSpec extends mutable.Specification with CassandraCluster {

  def start = {
    scala.util.Try(session.execute("DROP KEYSPACE blackpepper;"))
    session.execute("CREATE KEYSPACE blackpepper WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};")
    session.execute("use blackpepper;")
    val recipesTable =
      """|CREATE TABLE recipes (
        |url text PRIMARY KEY,
        |description text,
        |ingredients list<text>,
        |author text,
        |servings int,
        |tags set<text>,
        |last_checked_at timestamp,
        |props map<text, text>,
        |uid timeuuid,
        |difficulty text);
      """.stripMargin
    session.execute(recipesTable)
  }

  override def map(fs: => Fragments) = Step(start) ^ fs ^ Step(cluster.close())
}
