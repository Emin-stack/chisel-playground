// import Mill dependency
import mill._
import mill.define.Sources
import mill.modules.Util
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.TestModule.ScalaTest
import scalalib._
// support BSP
import mill.bsp._

object v {
  val scala = "2.13.14"
  val chisel = "7.0.0-M2"
  val chiseltest = ivy"edu.berkeley.cs::chiseltest:6.0.0"
}

object playground extends SbtModule with ScalafmtModule { m =>
  override def millSourcePath = os.pwd
  override def scalaVersion = v.scala
  override def scalacOptions = Seq(
    "-language:reflectiveCalls",
    "-deprecation",
    "-feature",
    "-Xcheckinit"
  )
  override def ivyDeps = Agg(
    ivy"org.chipsalliance::chisel:${v.chisel}"
  )
  override def scalacPluginIvyDeps = Agg(
    ivy"org.chipsalliance:::chisel-plugin:${v.chisel}"
  )
  object test extends SbtModuleTests with TestModule.ScalaTest {
    override def ivyDeps = m.ivyDeps() ++ Agg(
      ivy"org.scalatest::scalatest::3.2.16"
    )
  }
}
