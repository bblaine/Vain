import sbt._

class VainProject(info: ProjectInfo) extends DefaultProject(info)
{
  val apacheHttp = "org.apache.httpcomponents" % "httpclient" % "4.1"
  
  val liftjson = "net.liftweb" % "lift-json_2.8.1" % "2.2"
}
