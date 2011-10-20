package jettypult

import org.eclipse.jetty._
import server.nio.SelectChannelConnector
import server.Server
import java.io.File
import org.eclipse.jetty.webapp.WebAppContext

/**
 * Used to launch web applications using Jetty.
 *
 * @author Markus Kahl
 * @version: 0.1
 *
 * 20.10.11
 */

object Launcher {

  def main(args: Array[String]) {
    val ports = {
      val numbers = get(args, "p", "port").map(_.split(",").toList).map(_.map(_.toInt)) getOrElse List(8080)
      numbers.toStream ++ Stream.from(numbers.last + 1)
    }
    val files = args.map(new File(_)).reverse.takeWhile(isWebApp).reverse.toList
    val servers = (files zip ports).map { case (file, port) =>
      println("Starting "+file+" on port "+port+" ...")
      val server = createServer
      server.addConnector(createConnector(port))
      server.setHandler(createWebAppContext(file))
      server.start
      server
    }
    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run {
        println("Process terminated")
      }
    })
    servers.foreach(_.join)
  }

  def get(args: Array[String], shortName: String, longName: String = null) = args.toList.tails.takeWhile(_.size >= 2).flatMap { tail =>
    val opt :: value :: _ = tail
    if ((opt == "-"+shortName) || (opt == "--"+longName)) Some(value)
    else None
  }.toList.headOption

  def isWebApp(file: File) = file.exists && (file.getName.endsWith(".war") || file.list.contains("WEB-INF"))

  def createServer = new Server

  def createConnector(port: Int = 8080) = {
    val scc = new SelectChannelConnector
    scc.setPort(port)
    scc
  }

  def createWebAppContext(file: File) = {
    val ctx = new WebAppContext
    if (file.getName endsWith ".war") {
      ctx.setWar(file.getAbsolutePath)
    } else {
      ctx.setResourceBase(file.getAbsolutePath);
      ctx.setDescriptor(file.getAbsolutePath+"/WEB-INF/web.xml");
      ctx.setParentLoaderPriority(true);
    }
    ctx.setContextPath("/")
    ctx
  }
}