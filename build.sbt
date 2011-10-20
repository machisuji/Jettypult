name := "Jettypult"

version := "0.1"

scalaVersion := "2.9.1"

mainClass := Some("jettypult.Launcher")

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % "7.4.5.v20110725" % "compile"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "compile"
