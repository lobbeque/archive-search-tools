name := "archive-search-tools"

organization := "qlobbe"

version := "1.0.0"

description := "Search tools for solr"

publishMavenStyle := true

autoScalaLibrary := false

mainClass in Compile := Some("qlobbe.ArchiveReader")

libraryDependencies ++= Seq(
   "org.apache.solr" % "solr-core" % "5.4.1"
)
