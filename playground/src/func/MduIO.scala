package func

import chisel3._

class MulDivIO extends Bundle {
  val in = Vec(2, Input(UInt(64.W)))
  val sign = Input(Bool())
  val isRem = Input(Bool())
  val result = Output(UInt(64.W))
}

class MduInput extends Bundle {
  val rs1 = Input(UInt(64.W))
  val rs2 = Input(UInt(64.W))
  val op = Input(UInt(3.W))
}

class MduOutput extends Bundle {
  val result = Output(UInt(64.W))
}

class MduIO extends Bundle {
  val in = new MduInput
  val out = new MduOutput
}
