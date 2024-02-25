package func

import chisel3._

trait MduConsts {
  object MduOp {
    val mul:  UInt = "b000".U
    val div:  UInt = "b001".U
    val divu: UInt = "b101".U
    val rem:  UInt = "b011".U
    val remu: UInt = "b111".U

    def isMul(op: UInt) = op === mul

    def isDiv(op: UInt) = op(0)

    def isRem(op: UInt) = op(1)

    def isSigned(op: UInt) = !op(2)
  }
}
