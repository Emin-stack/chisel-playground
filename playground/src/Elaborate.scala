import circt.stage._

object Elaborate extends App {
  def top = new GCD()
  val useMFC = true // use MLIR-based firrtl compiler
 // val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))
 // use --split-verilog 
  val generator =
    Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top), firrtl.EmitAllModulesAnnotation(classOf[firrtl.VerilogEmitter]),Fir    toolOption("--split-verilog"), FirtoolOption("-o=build"))
  if (useMFC) {
    (new ChiselStage).execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
  } else {
    (new chisel3.stage.ChiselStage).execute(args, generator)
  }
}
