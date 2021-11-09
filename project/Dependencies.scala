import sbt._

object Versions {
  val LogbackVersion            = "1.2.3"
  val JAI                       = "1.1.3"
  val JtsVersion                = "1.16.1"
  val Log4sVersion              = "1.8.2"
  val GeoTrellisVersion         = "3.5.2"
  val VectorPipeVersion         = "2.1.3"
  val DeclineVersion            = "1.0.0"
  val ScalaGraph                = "1.12.5"
  val SparkVersion              = "2.4.4"
  val GeomesaVersion            = "2.3.0"
  val ScalaTagsVersion          = "0.6.8"
}

object Dependencies {
  val geotrellisVector   = "org.locationtech.geotrellis" %% "geotrellis-vector"   % Versions.GeoTrellisVersion
  val log4s              = "org.log4s"                   %% "log4s"               % Versions.Log4sVersion
  val logbackClassic     = "ch.qos.logback"              % "logback-classic"      % Versions.LogbackVersion
  val jaiCore            = "javax.media"                 % "jai_core"             % Versions.JAI
  //val jts                = "org.locationtech.jts"        % "jts-core"             % Versions.JtsVersion
  val scalaTags          = "com.lihaoyi"                 %% "scalatags"           % Versions.ScalaTagsVersion
  val sparkHive          = "org.apache.spark"            %% "spark-hive"          % Versions.SparkVersion
  val sparkStreaming     = "org.apache.spark"            %% "spark-streaming"     % Versions.SparkVersion
  val sparkJts           = "org.locationtech.geomesa"    %% "geomesa-spark-jts"   % Versions.GeomesaVersion
}
