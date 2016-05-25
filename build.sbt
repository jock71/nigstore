name := """Nigstore"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

pipelineStages := Seq(uglify, digest, gzip)

pipelineStages in Assets := Seq()

pipelineStages := Seq(uglify, digest, gzip)

DigestKeys.algorithms += "sha1"

UglifyKeys.uglifyOps := { js =>
  Seq((js.sortBy(_._2), "concat.min.js"))
}


resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  //"com.google.inject" % "guice" % "4.0",
  "org.scaldi" %% "scaldi-play" % "0.5.7",
  "javax.inject" % "javax.inject" % "1",
  //"org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "org.reactivemongo" % "play2-reactivemongo_2.11" % "0.11.11-play24",
  "org.webjars" % "bootstrap" % "3.3.4",
  "org.webjars" % "angularjs" % "1.3.15",
  "org.webjars" % "angular-ui-bootstrap" % "0.13.0",
  "org.mockito" % "mockito-core" % "1.10.19" % "test")


routesGenerator := InjectedRoutesGenerator