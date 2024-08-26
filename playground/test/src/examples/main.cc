#include "Vtb.h"
#include <verilated.h>
#include <verilated_vcd_c.h>

int main(int argc, char **argv) {
  Verilated::debug(0);
  VerilatedContext *contextp = new VerilatedContext;
  VerilatedVcdC *tracep = new VerilatedVcdC;
  Vtb *duvp = new Vtb;

  contextp->fatalOnError(false);
  contextp->commandArgs(argc, argv);
  contextp->traceEverOn(true);
  duvp->trace(tracep, 3);
  tracep->open("waveform.vcd");
  while (!contextp->gotFinish()) {
    duvp->eval();
    tracep->dump(contextp->time());
    contextp->timeInc(1);
    if (!duvp->eventsPending())
      break;
    contextp->time(duvp->nextTimeSlot());
  }
  if (!contextp->gotFinish()) {
    VL_DEBUG_IF(VL_PRINTF("+ Exiting without $finish; no events left\n"););
  }
  tracep->close();
  delete duvp;
  return 0;
}
