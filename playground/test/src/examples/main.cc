#include "Vtb.h"
#include <verilated.h>
#ifdef TRACE
#include <verilated_fst_c.h>
#endif

int main(int argc, char **argv) {
  // Set debug level, 0 is off, 9 is highest presently used
  Verilated::debug(0);

  // Construct a VerilatedContext to hold simulation time
  VerilatedContext *contextp = new VerilatedContext;

  // Construct the Verilated model, from Vtb.h
  Vtb *duvp = new Vtb;

  // Set to throw fatal error on $stop/non-fatal error
  contextp->fatalOnError(false);

  // Pass arguments
  contextp->commandArgs(argc, argv);

#ifdef TRACE
  // Construct a VerilatedFstC
  // Enable fst trace by using `--trace-fst`: https://veripool.org/guide/latest/exe_verilator.html?highlight=fst#cmdoption-trace-fst
  VerilatedFstC *tracep = new VerilatedFstC;

  // Trace the model
  contextp->traceEverOn(true);

  // Set trace depth
  duvp->trace(tracep, 3);

  // Open fst file for tracing
  tracep->open("waveform.fst");
#endif

  // Simulate util $finish
  while (!contextp->gotFinish()) {
    // Evaluate model
    duvp->eval();
#ifdef TRACE
    // Dump signal changes every time step
    tracep->dump(contextp->time());
    contextp->timeInc(1);
#endif
    // Using delays and `--timing`, we should call additional two methods:
    // `eventsPending()` and `nextTimeSlot()`
    // see: https://veripool.org/guide/latest/connecting.html#wrappers-and-model-evaluation-loop

    // `eventsPending()`: Check if there are any delayed events pending
    if (!duvp->eventsPending())
      break;

    // `nextTimeSlot()`: Returns the simulation time of the next delayed event.
    contextp->time(duvp->nextTimeSlot());
  }

  // Output messages when quitting the simulation before $finish
  // About how to enbale VL_DEBUG_IF message: https://github.com/verilator/verilator/issues/2175
  if (!contextp->gotFinish()) {
    VL_DEBUG_IF(VL_PRINTF("+ Exiting without $finish; no events left\n"););
  }
#ifdef TRACE
  // Cloase the trace file
  tracep->close();
#endif

  // Delete duvp
  delete duvp;
  return 0;
}
