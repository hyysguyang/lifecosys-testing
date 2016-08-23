import com.lifecosys.sbt.JavaCodeFormatterPlugin.JavaCodeFormatterKeys._
import sbt.Keys._
import sbt.{IO, _}

/**
  * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
  * @author <a href="mailto:Young.Gu@lifcosys.com">Young Gu</a>
  */
object BuildSettings {
  val VERSION = "0.1"

  val lifecycle =
    addCommandAlias("install", ";formatJava;scalariformFormat;compile;test") ++
      addCommandAlias("testing", ";clean;scalariformFormat;compile;test")

  lazy val projectBuildSettings = basicSettings ++ scalaFormattingSettings ++ publishSettings

  val basicSettings = Defaults.coreDefaultSettings ++ lifecycle ++ Seq(
    version := VERSION,
    homepage := Some(new URL("https://lifecosys.com/developer/lifecosys-testing")),
    organization := "com.lifecosys",
    organizationHomepage := Some(new URL("https://lifecosys.com")),
    description := "Reusable testing utility.",
    licenses += ("Apache 2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    startYear := Some(2016),
    scalaVersion := "2.11.6",
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    javaOptions += s"-source 1.8 -target 1.8 -Xlint -parameters",
    crossPaths := false,
    autoScalaLibrary := false
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


  val javaFormattingSettings = {
    val online: URL = new URL("https://raw.githubusercontent.com/hyysguyang/java-code-formatter/master/sample-style/JavaConventions-variant.xml")
    val codingStyle = IO.temporaryDirectory / "eclipse-codingstyle.xml"
    if (!codingStyle.exists()) IO.download(online, codingStyle)
    List(
      eclipseProfileFile in javaCodeFormatter := Some(codingStyle)
    )
  }

  import bintray.BintrayKeys._
  val publishSettings = Seq(
    credentials += Credentials(Path.userHome / ".bintray" / ".credentials"),
    publishMavenStyle := false,
    bintrayOrganization in bintray := None,
    pomExtra :=
      <url>https://github.com/hyysguyang/java-code-formatter</url>
        <licenses>
          <license>
            <name>Apache 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/hyysguyang/java-code-formatter.git</url>
          <connection>scm:https://github.com/hyysguyang/java-code-formatter.git</connection>
        </scm>
        <developers>
          <developer>
            <id>hyysguyang</id>
            <name>Young Gu</name>
            <url>https://plus.google.com/u/0/+YoungGu</url>
          </developer>
        </developers>
  )


}
