import circt.stage._

object Elaborate extends App {
  // use MLIR-based firrtl compiler
  val generator =
    Array(
      "--split-verilog",
      "-o=build/",
      "--disable-annotation-unknown",
      "--lowering-options=disallowLocalVariables,disallowPackedArrays,locationInfoStyle=wrapInAtSquareBracket"
    )
  ChiselStage.emitSystemVerilogFile(new GCD(), Array("--target-dir", "build/", "--split-verilog"), generator)
}
