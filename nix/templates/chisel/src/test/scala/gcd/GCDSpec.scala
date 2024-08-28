package gcd

import chisel3._
import chisel3.simulator._
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers
import svsim._

class VerilatorSimulator(val workspacePath: String) extends SingleBackendSimulator[verilator.Backend] {
  val backend = verilator.Backend.initializeFromProcessEnvironment()
  val tag = "verilator"
  val commonCompilationSettings = CommonCompilationSettings()
  val backendSpecificCompilationSettings = verilator.Backend.CompilationSettings()
}

class SimulatorSpec extends AnyFunSpec with Matchers {
  describe("Chisel Simulator") {
    it("runs GCD correctly") {
      val simulator = new VerilatorSimulator("test_run_dir/simulator/GCDSimulator")
      val result = simulator
        .simulate(new GCD()) { module =>
          val gcd = module.wrapped
          val a = module.port(gcd.io.value1)
          val b = module.port(gcd.io.value2)
          val loadValues = module.port(gcd.io.loadingValues)
          val result = module.port(gcd.io.outputGCD)
          val resultIsValid = module.port(gcd.io.outputValid)
          val clock = module.port(gcd.clock)
          a.set(24)
          b.set(36)
          loadValues.set(1)
          clock.tick(
            inPhaseValue = 0,
            outOfPhaseValue = 1,
            timestepsPerPhase = 1,
            cycles = 1
          )
          loadValues.set(0)
          clock.tick(
            inPhaseValue = 0,
            outOfPhaseValue = 1,
            timestepsPerPhase = 1,
            maxCycles = 10,
            sentinel = Some(resultIsValid, 1)
          )
          result.get().asBigInt
        }
        .result
      assert(result === 12)
    }

    it("runs GCD correctly with peek/poke") {
      val simulator = new VerilatorSimulator("test_run_dir/simulator/GCDSimulator")
      val result = simulator
        .simulate(new GCD()) { module =>
          import PeekPokeAPI._
          val gcd = module.wrapped
          gcd.io.value1.poke(24.U)
          gcd.io.value2.poke(36.U)
          gcd.io.loadingValues.poke(1.B)
          gcd.clock.step()
          gcd.io.loadingValues.poke(0.B)
          gcd.clock.step(10)
          gcd.io.outputGCD.expect(12)
          gcd.io.outputGCD.peek().litValue
        }
        .result
      assert(result === 12)
    }
  }
}
