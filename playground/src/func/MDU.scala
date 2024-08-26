package func

import chisel3._
import chisel3.util._

class Multiplier(len: Int = 64) extends Module with MduConsts {
  val io = IO(new MulDivIO)
  val isSigned = io.sign
  val (a, b) = (io.in(0), io.in(1))
  io.result := a * b
}

class Divider(len: Int = 64) extends Module with MduConsts {
  val io = IO(new MulDivIO)

  def abs(a: UInt, sign: Bool): (Bool, UInt) = {
    val s = a(len - 1) && sign
    (s, Mux(s, -a, a))
  }

  val isSigned = io.sign
  val (a, b) = (io.in(0), io.in(1))
  val (aSigned, aAbs) = abs(a, isSigned)
  val (bSigned, bAbs) = abs(b, isSigned)
  val unsignedResult = aAbs / bAbs
  val unsignedRemain = aAbs % bAbs
  val remianResult = Mux(aSigned, -unsignedRemain, unsignedRemain)
  val signedResult = Mux(aSigned ^ bSigned, -unsignedResult, unsignedResult)
  io.result := Mux(io.isRem, remianResult, signedResult)
}

class MDU(len: Int = 64) extends Module with MduConsts {
  val io = IO(new MduIO)
  val isMul = MduOp.isMul(io.in.op)
  val isSigned = MduOp.isSigned(io.in.op)
  val isRem = MduOp.isRem(io.in.op)
  val isDiv = MduOp.isDiv(io.in.op)
  val divider = Module(new Divider(64))
  val multiplier = Module(new Multiplier(64))
  divider.io.sign := isSigned
  divider.io.isRem := isRem
  divider.io.in(0) := io.in.rs1
  divider.io.in(1) := io.in.rs2
  val divResult = divider.io.result

  multiplier.io.in(0) := io.in.rs1
  multiplier.io.in(1) := io.in.rs2
  multiplier.io.sign := isSigned
  multiplier.io.isRem := DontCare
  val mulResult = multiplier.io.result
  val resSelect = Cat(isDiv, isMul, (isMul === 0.U) && (isDiv === 0.U))
  io.out.result := Mux1H(
    Seq(
      resSelect(0) -> 0.U(len.W),
      resSelect(1) -> mulResult,
      resSelect(2) -> divResult
    )
  )
}
