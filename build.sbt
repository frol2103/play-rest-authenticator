name := """play-rest-authenticator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .configs( IntegrationTest )
  .settings( Defaults.itSettings : _*)

scalaSource in IntegrationTest := baseDirectory.value / "it/scala"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "Atlassian Releases" at "https://maven.atlassian.com/public/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Maven centran" at  "http://central.maven.org/maven2/",
  Resolver.sonatypeRepo("snapshots")
 )

pipelineStages := Seq(digest,gzip)

libraryDependencies ++= Seq(
  cache,
  filters,
  "com.typesafe.play" %% "play-mailer" % "3.0.1",
  "com.mohiva" %% "play-silhouette" % "3.0.0",
  "org.webjars" %% "webjars-play" % "2.4.0",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.0" % "test",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
  specs2 % Test,
  specs2 % IntegrationTest,
  "org.scalatest" %% "scalatest" % "3.0.1" % IntegrationTest,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test,
  "info.cukes" %% "cucumber-scala" % "1.2.5" % IntegrationTest,
  "info.cukes" % "cucumber-junit" % "1.2.5" % IntegrationTest,
  "info.cukes" % "cucumber-guice" % "1.2.5" % IntegrationTest,
  "org.scalaj" %% "scalaj-http" % "2.3.0" % IntegrationTest

)

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-nullary-override",
  "-Ywarn-value-discard",
  "-language:reflectiveCalls"
)

