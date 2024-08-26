// import Mill dependency
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import mill.scalalib.TestModule.Utest
// support BSP
import mill.bsp._

object v {
  val scala = "2.13.14"
  val chisel = "7.0.0-M2"
  val chiseltest = ivy"edu.berkeley.cs::chiseltest:6.0.0"
}

object playground extends ScalaModule with ScalafmtModule { m =>
  val useChisel6 = true
  val useUTest = false // utest support will be removed: https://github.com/ucb-bar/chiseltest/pull/688
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
  object test extends ScalaTests {
    override def ivyDeps = m.ivyDeps() ++ Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      v.chiseltest
    )
    def testFramework =
      "utest.runner.Framework"
  }
}
