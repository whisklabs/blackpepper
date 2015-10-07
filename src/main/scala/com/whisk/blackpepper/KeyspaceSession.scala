package com.whisk.blackpepper

import com.datastax.driver.core.Cluster

abstract class KeyspaceSession(cluster: Cluster, name: String) {

  lazy val session = cluster.connect(name)
}
