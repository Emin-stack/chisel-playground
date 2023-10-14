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
    // fix firtool creates empty sv files when it is passed "-o"
    // https://discourse.llvm.org/t/firtool-creates-empty-sv-files-when-it-is-passed-o/73302
    // If you are using an older chisel than version 5.x, see: https://github.com/llvm/circt/issues/4252#issuecomment-1304389199
    ChiselStage.emitSystemVerilogFile(new GCD(), Array("--target-dir", "build/", "--split-verilog"), generator)
}
