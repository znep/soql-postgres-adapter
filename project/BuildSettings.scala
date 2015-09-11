import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys

object BuildSettings {
  val buildSettings: Seq[Setting[_]] = Seq(
    fork in test := true,
    scalacOptions ++= Seq("-language:implicitConversions"),
    BuildInfoKeys.buildInfoPackage := "com.socrata.pg"
  )

  val projectSettings = buildSettings ++
    Seq(
      // TODO: enable code coverage build failures
      scoverage.ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false
    )
}
