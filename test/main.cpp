#include <verilated.h>
#include <verilated_vcd_c.h>
#include <stdio.h>
#include <assert.h>
// DPI-C 
//#include "VCore__Dpi.h"
#include <svdpi.h>
#include <obj_dir/VCore___024root.h>
#include <obj_dir/VCore.h>

static VCore *dut;
VerilatedContext *contextp = NULL;
VerilatedVcdC *tfp = NULL;

bool flag = false;
int counter = 0;

// example for DPI-C
// extern int ebreak( int* inst);
/*
 extern int svwrite(svBitVecVal* inst);

 extern void ebreak(const svBitVecVal* inst)
 {
     if (*inst == 0x00100073)
     {
         counter++;
         if (counter==16)flag = true;
     }
 }
*/

void step()
{
    dut->clock = 0;
    dut->eval();
    contextp->timeInc(1);
    tfp->dump(contextp->time());
    dut->clock = 1;
    dut->eval();
    contextp->timeInc(1);
    tfp->dump(contextp->time());
}

void load_prog(const char *bin) {
    FILE *fp = fopen(bin, "r");
    int ret = fread(&dut->rootp->Core__DOT__ifu__DOT__M__DOT__M_ext__DOT__Memory, 1, 1024, fp);
    assert(ret);
    fclose(fp);
}

void sim_init()
{
    
    dut = new VCore;
    contextp = new VerilatedContext;
    tfp = new VerilatedVcdC;
    contextp->traceEverOn(true);
    dut->trace(tfp, 1);
    tfp->open("dump.vcd");
    //  dut->reset = 0;
    // dut->clock = 0;
}

void reset(int n) 
{ 
    dut->reset = 1; 
    while (n --) { step(); } 
    dut->reset = 0; 
}

int main(int argc, char *argv[])
{
    Verilated::traceEverOn(true);
    sim_init();
    load_prog(argv[1]);
    reset(5);

  //---------- put your code here. ----------//
    while (!dut->io_halt&& !flag)
    {
        step();
        counter++;
        
    }
    
    tfp->close();
    return 0;
}
