#include "Vtb.h"
#include <verilated.h>
#ifdef TRACE
#include <verilated_fst_c.h>
#endif

int main(int argc, char **argv) {
  Verilated::debug(0);
  VerilatedContext *contextp = new VerilatedContext;
  Vtb *duvp = new Vtb;

  contextp->fatalOnError(false);
  contextp->commandArgs(argc, argv);
#ifdef TRACE
  VerilatedFstC *tracep = new VerilatedFstC;
  contextp->traceEverOn(true);
  duvp->trace(tracep, 3);
  tracep->open("waveform.fst");
#endif
  while (!contextp->gotFinish()) {
    duvp->eval();
#ifdef TRACE
    tracep->dump(contextp->time());
    contextp->timeInc(1);
#endif
    if (!duvp->eventsPending())
      break;
    contextp->time(duvp->nextTimeSlot());
  }
  if (!contextp->gotFinish()) {
    VL_DEBUG_IF(VL_PRINTF("+ Exiting without $finish; no events left\n"););
  }
#ifdef TRACE
  tracep->close();
#endif
  delete duvp;
  return 0;
}
