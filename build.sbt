name := "play_for_scala"

version := "1.0"

lazy val `play_for_scala` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

scalaVersion := "2.12.2"

val appDependencies = Seq(
    "net.sf.barcode4j" % "barcode4j" % "2.0",
    jdbc,
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "org.playframework.anorm" %% "anorm" % "2.6.2",
    cacheApi,
    ehcache
)
libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)
libraryDependencies ++= appDependencies
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M1" % Test

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")



      