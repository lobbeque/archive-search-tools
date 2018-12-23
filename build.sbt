import AssemblyKeys._

assemblySettings

name := "archive-search-tools"

organization := "qlobbe"

version := "1.0.0"

description := "Search tools for solr"

publishMavenStyle := true

autoScalaLibrary := false

mainClass in Compile := Some("qlobbe.ClusterParallelExpandComponent")

libraryDependencies ++= Seq(
   	"joda-time" % "joda-time" % "2.9.2",
   	"org.apache.solr" % "solr-core" % "5.4.1"
)
