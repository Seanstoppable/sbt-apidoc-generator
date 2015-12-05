package com.seanstoppable

import sbt._
import Keys._

import scala.io.Source

import play.api.libs.json._

object ApiDocPlugin extends AutoPlugin {

  object autoImport {
    val generateScala = taskKey[Unit]("Generate Scala Client")
    val apidocLocation = settingKey[String]("Path to apidoc file")
    val outputLocation = settingKey[String]("Path to generated output")
  }

  import autoImport._

  override def projectSettings = Seq(
    apidocLocation := "api.json",
    outputLocation := ".",
    generateScala := {
      generateScalaClient(apidocLocation.value, outputLocation.value, organization.value, version.value)
    }
  )

  private def generateScalaClient(apiLocation: String, outputFolder: String, packageName: String, version: String): Unit = {
    import builder.OriginalValidator
    import builder.api_json._
    import com.bryzek.apidoc.generator.v0.models._
    import com.bryzek.apidoc.api.v0.models.{Original, OriginalType}
    import com.bryzek.apidoc.spec.v0.models._
    import com.bryzek.apidoc.spec.v0.models.json._
    import db.Authorization
    import lib.{DatabaseServiceFetcher, ServiceConfiguration}
    import scala.models._

    val apidoc = Source.fromFile(apiLocation).mkString

    val service: Either[Seq[String], Service] = OriginalValidator(
      ServiceConfiguration("", packageName, version),
      Original (OriginalType.ApiJson, apidoc), //also make this configurable at some point
      DatabaseServiceFetcher(Authorization.All)
    ).validate()

    service match {
      case Left(errors: Seq[String]) => errors.foreach { error =>
        println(error)
      }
      case Right(service: Service) => {

        val invocationForm = InvocationForm(service)
        Play24ClientGenerator.invoke(invocationForm) match {
          case Left(x: Seq[String]) => println(x)
          case Right(x: Seq[File]) => x.foreach { file =>

            import java.nio.file.{Paths, Files}
            import java.nio.charset.StandardCharsets
            Files.write(Paths.get(s"$outputFolder/${file.name}"), file.contents.getBytes(StandardCharsets.UTF_8))
          }
        }
      }
    }
  }

}
