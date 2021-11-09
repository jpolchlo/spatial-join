package com.azavea.spatialjoin

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.locationtech.geomesa.spark.jts._
import org.scalatest._

trait TestEnvironment {

  def sparkMaster = "local[2]"

  lazy val spark: SparkSession = {
    System.setProperty("spark.driver.port", "0")
    System.setProperty("spark.hostPort", "0")
    System.setProperty("spark.ui.enabled", "false")

    val conf = new SparkConf()
    conf
      .setMaster(sparkMaster)
      .setAppName("Test Context")
      .set("spark.default.parallelism", "4")

    val session = SparkSession.builder().config(conf).getOrCreate()

    System.clearProperty("spark.driver.port")
    System.clearProperty("spark.hostPort")
    System.clearProperty("spark.ui.enabled")

    session.withJTS
  }

}
