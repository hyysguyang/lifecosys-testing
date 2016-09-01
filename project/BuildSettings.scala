import sbt.Keys._
import sbt._

/**
  * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
  * @author <a href="mailto:Young.Gu@lifcosys.com">Young Gu</a>
  */
object BuildSettings {
  val VERSION = "0.2-SNAPSHOT"

  val lifecycle =
    addCommandAlias("install", ";formatJava;scalariformFormat;compile;test") ++
      addCommandAlias("testing", ";clean;scalariformFormat;compile;test")

  lazy val projectBuildSettings = basicSettings ++ scalaFormattingSettings

  val basicSettings = Defaults.coreDefaultSettings ++ lifecycle ++ Seq(
    version := VERSION,
    homepage := Some(new URL("https://lifecosys.com/developer/lifecosys-testing")),
    organization := "com.lifecosys",
    organizationHomepage := Some(new URL("https://lifecosys.com")),
    description := "Reusable testing utility.",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    startYear := Some(2016),
    scalaVersion := "2.11.6",
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    crossPaths := false
  )

  val scalaFormattingSettings = {
    import com.typesafe.sbt.SbtScalariform.ScalariformKeys

    import scalariform.formatter.preferences._
    ScalariformKeys.preferences :=
      FormattingPreferences()
        .setPreference(AlignParameters, true)
        .setPreference(CompactStringConcatenation, true)
        .setPreference(CompactControlReadability, false)
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
        .setPreference(SpacesWithinPatternBinders, true)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(SpacesAroundMultiImports, true)
  }

}
