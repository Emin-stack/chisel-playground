package simulator

import chisel3._
import chisel3.simulator._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers
import svsim._
import PeekPokeAPI._
import func._

trait MduSimulate extends MduConsts {
  val width: Int = 64

  def ToUInt(x: Int): BigInt = {
    if (x < 0)
      BigInt(2).pow(width) + x
    else
      BigInt(x)
  }
  def testMdu(a: Int, b: Int, result: Int, op: UInt,simulator: VerilatorSimulator): Unit = {
    val output = simulator
      .simulate(new MDU(64)) { controller =>
        val mdu = controller.wrapped
        mdu.io.in.op.poke(op)
        mdu.io.in.rs1.poke(a)
        mdu.io.in.rs2.poke(b)
        println(
          "(func.MDU) input value1 :0x%X".format(mdu.io.in.rs1.peek().litValue)
            + "\tinput value2 :0x%X".format(mdu.io.in.rs2.peek().litValue)
        )
        println("(func.MDU) rem output value :0x%X".format(mdu.io.out.result.peek().litValue))
        println("(func.MDU) dut should output :0x%X".format(ToUInt(result)))
        mdu.io.out.result.expect(ToUInt(result))
        mdu.io.out.result.peek().litValue
      }
      .result
    output
  }
}
class VerilatorSimulator(val workspacePath: String) extends SingleBackendSimulator[verilator.Backend] {
  val backend                            = verilator.Backend.initializeFromProcessEnvironment()
  val tag                                = "verilator"
  val commonCompilationSettings          = CommonCompilationSettings()
  val backendSpecificCompilationSettings = verilator.Backend.CompilationSettings()
}

class MduSimulatorSpec extends AnyFunSpec with Matchers with MduSimulate {
  describe("Chisel Simulator") {
    it("runs func.MDU divide correctly") {
      val simulator = new VerilatorSimulator("build/simulator/GCDSimulator")
      val testData: List[(Int, Int)] = List[(Int, Int)](
        (1, 2),
        (3, 4),
        (4, 5),
        (-1, -1),
        (-2, -3),
        (-2,  2),
      )
      testData.foreach {
        case (a, b) =>
          val result = a / b
          val output = testMdu(a, b, result, MduOp.div, simulator)
          output
      }
      testData.foreach {
        case (b, a) =>
          val result = a / b
          val output = testMdu(a, b, result, MduOp.div, simulator)
          output
      }
    }
    it("runs func.MDU remu correctly") {
      val simulator = new VerilatorSimulator("build/simulator/GCDSimulator")
      val testData: List[(Int, Int)] = List[(Int, Int)](
        (1, 2),
        (3, 4),
        (4, 5),
        (-1, -1),
        (-2, -3),
      )
      testData.foreach {
        case(a, b) =>
          val result = a % b
          val output = testMdu(a, b, result, MduOp.rem,simulator)
          output
      }
      testData.foreach {
        case(b, a) =>
          val result = a % b
          val output = testMdu(a, b, result, MduOp.rem, simulator)
          output
      }
    }
    it("runs func.MDU mul correctly") {
      val simulator = new VerilatorSimulator("build/simulator/GCDSimulator")
      val testData: List[(Int, Int)] = List[(Int, Int)](
        (1, 2),
        (3, 4),
        (4, 5),
        (-1, -1),
        (-2, -3),
      )
      testData.foreach {
        case(a, b) =>
          val result = a * b
          val output = testMdu(a, b, result, MduOp.mul, simulator)
          output
      }
      testData.foreach {
        case(b, a) =>
          val result = a * b
          val output = testMdu(a, b, result, MduOp.mul, simulator)
          output
      }
    }
  }
}
