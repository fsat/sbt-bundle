import org.scalatest.Matchers._
import com.typesafe.sbt.bundle.SbtBundle._

lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)

name := "simple-test"

version := "0.1.0-SNAPSHOT"

BundleKeys.nrOfCpus := 1.0
BundleKeys.memory := "64m"
BundleKeys.diskSpace := "10m"
BundleKeys.roles := Set("web-server")

BundleKeys.endpoints := Map(
  "web" -> Endpoint("http", 0, 9000, "/simple-test"),
  "other" -> Endpoint("http", 0, 9001, "/simple-test")
)

val checkBundleConf = taskKey[Unit]("check-main-css-contents")

checkBundleConf := {
  val contents = IO.read(target.value / "typesafe-conductr" / "tmp" / "bundle.conf")
  val expectedContents = """|version    = "1.0.0"
                            |system     = "simple-test-0.1.0-SNAPSHOT"
                            |nrOfCpus   = 1.0
                            |memory     = 67108864
                            |diskSpace  = 10485760
                            |roles      = ["web-server"]
                            |components = {
                            |  "simple-test-0.1.0-SNAPSHOT" = {
                            |    description      = "simple-test"
                            |    file-system-type = "universal"
                            |    start-command    = ["simple-test-0.1.0-SNAPSHOT/bin/simple-test", "-J-Xms67108864", "-J-Xmx67108864"]
                            |    endpoints        = {
                            |      "web" = {
                            |        protocol     = "http"
                            |        bind-port    = 0
                            |        service-port = 9000
                            |        service-name = "/simple-test"
                            |      },
                            |      "other" = {
                            |        protocol     = "http"
                            |        bind-port    = 0
                            |        service-port = 9001
                            |        service-name = "/simple-test"
                            |      }
                            |    }
                            |  }
                            |}""".stripMargin
  contents should include(expectedContents)
}
