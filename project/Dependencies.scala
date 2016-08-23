import sbt._

/**
  * @author <a href="mailto:hyysguyang@gmail.com">Young Gu</a>
  * @author <a href="mailto:Young.Gu@lifcosys.com">Young Gu</a>
  */
object Dependencies {

  val seleniumjava = "org.seleniumhq.selenium" % "selenium-java" % "2.45.0"
  val ashot = "ru.yandex.qatools.ashot" % "ashot" % "1.4.12"

  private val springVersion: String = "4.3.2.RELEASE"
  val springContext = "org.springframework" % "spring-context" % springVersion
  val springJDBC = "org.springframework" % "spring-jdbc" % springVersion
  val springTest = "org.springframework" % "spring-test" % springVersion

  val commonslang = "org.apache.commons" % "commons-lang3" % "3.4"
  val commonsIo = "commons-io" % "commons-io" % "2.4"
  val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"

  val p6spy = "p6spy" % "p6spy" % "2.3.0"


  // Java related dependency
  val javaslang = "io.javaslang" % "javaslang" % "2.0.2"
  val javaslangJackson = "io.javaslang" % "javaslang-jackson" % "2.0.2"

  val jacksonModuleParameterNames = "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.8.0"
  val jacksonDatatypeJdk8 = "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.8.0"

  val junit = "junit" % "junit" % "4.12"
  val junitInterface = "com.novocode" % "junit-interface" % "0.11"
  val assertjCore = "org.assertj" % "assertj-core" % "3.5.1"
  val fluentleniumAssert = "org.fluentlenium" % "fluentlenium-assertj" % "0.13.1"

  // Scala related dependency
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.5"

  def basicDependencies = compile(seleniumjava, ashot, springContext,springJDBC, springTest, commonslang, commonsIo, logback, p6spy)

  def javaDependencies = compile(javaslang, jacksonModuleParameterNames, jacksonDatatypeJdk8, junit, junitInterface, assertjCore, fluentleniumAssert)

  def scalaDependencies = compile(scalatest)

  def testDependencies = basicDependencies ++ javaDependencies ++ scalaDependencies


  def compile(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
}
