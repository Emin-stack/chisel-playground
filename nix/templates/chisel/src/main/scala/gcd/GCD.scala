package gcd

import chisel3._

/**
  * Compute GCD using subtraction method.
  * Subtracts the smaller from the larger until register y is zero.
  * value in register x is then the GCD
  */
class GCD extends Module {
  val io = IO(new Bundle {
    val value1 = Input(UInt(16.W))
    val value2 = Input(UInt(16.W))
    val loadingValues = Input(Bool())
    val outputGCD = Output(UInt(16.W))
    val outputValid = Output(Bool())
  })

  val x = Reg(UInt())
  val y = Reg(UInt())

  when(x > y) { x := x - y }.otherwise { y := y - x }

  when(io.loadingValues) {
    x := io.value1
    y := io.value2
  }

  io.outputGCD := x
  io.outputValid := y === 0.U
}

import circt.stage._

object GCD extends App {
  // use MLIR-based firrtl compiler
  val generator =
    Array(
      "--split-verilog",
      "-o=build/",
      "--disable-annotation-unknown",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays,locationInfoStyle=wrapInAtSquareBracket"
    )
  ChiselStage.emitSystemVerilogFile(new GCD(), Array("--target-dir", "build", "--split-verilog"), generator)
}
