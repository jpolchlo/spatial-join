cancelable in Global := true

lazy val scala211 = "2.11.12"

lazy val commonDependencies = Seq(
  Dependencies.jaiCore % Test from s"https://download.osgeo.org/webdav/geotools/javax/media/jai_core/${Versions.JAI}/jai_core-${Versions.JAI}.jar"
,
  Dependencies.logbackClassic,
  Dependencies.geotrellisVector,
  Dependencies.sparkHive % Provided,
  Dependencies.sparkJts
)

lazy val commonSettings = Seq(
  organization := "com.azavea",
  name := "spark-sql-spatial-join",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := scala211,
  //scalafmtOnCompile := true,
  scalacOptions := Seq(
    "-Ypartial-unification",
    // Required by ScalaFix
    "-Yrangepos",
    "-Ywarn-unused",
    //"-Ywarn-unused-import",
    "-target:jvm-1.8"
  ),
  autoCompilerPlugins := true,
  addCompilerPlugin("org.typelevel"   %% "kind-projector"     % "0.10.3"),
  addCompilerPlugin("com.olegpy"      %% "better-monadic-for" % "0.3.1"),
  addCompilerPlugin("org.scalamacros"  % "paradise"           % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin(scalafixSemanticdb),
  assembly / test := {},
  resolvers ++= Seq(
    "geosolutions" at "https://maven.geo-solutions.it/",
    "osgeo-releases" at "https://repo.osgeo.org/repository/release/"
  )
)

lazy val spatialJoinSettings = commonSettings ++ Seq(
  name := "spatial-join",
  run / fork := true,
  Test / fork := true,
  assembly / assemblyJarName := "spatial-join.jar",
  assembly / assemblyMergeStrategy := {
    case "reference.conf"                       => MergeStrategy.concat
    case "application.conf"                     => MergeStrategy.concat
    case n if n.startsWith("META-INF/services") => MergeStrategy.concat
    case n if n.endsWith(".SF") || n.endsWith(".RSA") || n.endsWith(".DSA") =>
      MergeStrategy.discard
    case "META-INF/MANIFEST.MF" => MergeStrategy.discard
    case _                      => MergeStrategy.first
  }
)

lazy val spatialJoin = (project in file("."))
  .settings(spatialJoinSettings: _*)
  .settings({libraryDependencies ++= commonDependencies
  })
