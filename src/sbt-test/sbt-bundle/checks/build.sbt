import ByteConversions._
import com.typesafe.sbt.bundle.SbtBundle._
import org.scalatest.Matchers._
import scala.concurrent.duration._

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

name := "simple-test"

version := "0.1.0-SNAPSHOT"

BundleKeys.nrOfCpus := 1.0
BundleKeys.memory := 64.MiB
BundleKeys.diskSpace := 10.MB
BundleKeys.checkInitialDelay := 1400.milliseconds
BundleKeys.checks := Seq(uri("$WEB_HOST?retry-count=5&retry-delay=3"))

val checkBundleConf = taskKey[Unit]("")

checkBundleConf := {
  val contents = IO.read(target.value / "bundle" / "bundle" / "tmp" / "bundle.conf")
  val expectedContents = """|version              = "1.1.0"
                            |name                 = "simple-test"
                            |compatibilityVersion = "0"
                            |system               = "simple-test"
                            |systemVersion        = "0"
                            |nrOfCpus             = 1.0
                            |memory               = 67108864
                            |diskSpace            = 10000000
                            |roles                = ["web"]
                            |components = {
                            |  simple-test = {
                            |    description      = "simple-test"
                            |    file-system-type = "universal"
                            |    start-command    = ["simple-test/bin/simple-test", "-J-Xms67108864", "-J-Xmx67108864"]
                            |    endpoints = {
                            |      "web" = {
                            |        bind-protocol = "http"
                            |        bind-port     = 0
                            |        services      = ["http://:9000"]
                            |      }
                            |    }
                            |  }
                            |}
                            |components = {
                            |  simple-test-status = {
                            |    description      = "Status check for the bundle component"
                            |    file-system-type = "universal"
                            |    start-command    = ["check", "--initial-delay", "2", "$WEB_HOST?retry-count=5&retry-delay=3"]
                            |    endpoints        = {}
                            |  }
                            |}""".stripMargin
  contents should include(expectedContents)
}
