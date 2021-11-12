import sbt._

object Versions {
  val GeomesaVersion            = "3.3.0"
  val GeoTrellisVersion         = "3.6.0"
  //val JAI                       = "1.1.3"
  val JtsVersion                = "1.16.1"
  val LogbackVersion            = "1.2.3"
  val Log4sVersion              = "1.8.2"
  val ScalaTagsVersion          = "0.6.8"
  val ScalaTestVersion          = "3.2.5"
  val SparkVersion              = "3.1.2"
}

object Dependencies {
  val geotrellisVector   = "org.locationtech.geotrellis" %% "geotrellis-vector"   % Versions.GeoTrellisVersion
  //val jaiCore            = "javax.media"                 % "jai_core"             % Versions.JAI
  //val jts                = "org.locationtech.jts"        % "jts-core"             % Versions.JtsVersion
  val log4s              = "org.log4s"                   %% "log4s"               % Versions.Log4sVersion
  val logbackClassic     = "ch.qos.logback"              % "logback-classic"      % Versions.LogbackVersion
  val scalaTags          = "com.lihaoyi"                 %% "scalatags"           % Versions.ScalaTagsVersion
  val scalatest          = "org.scalatest"               %% "scalatest"           % Versions.ScalaTestVersion
  val sparkHive          = "org.apache.spark"            %% "spark-hive"          % Versions.SparkVersion
  val sparkJts           = "org.locationtech.geomesa"    %% "geomesa-spark-jts"   % Versions.GeomesaVersion
  val sparkStreaming     = "org.apache.spark"            %% "spark-streaming"     % Versions.SparkVersion

  //val worksWithDependencies = Seq(jaiCore).map(_ % Provided)
}
