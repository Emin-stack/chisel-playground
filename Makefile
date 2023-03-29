BUILD_DIR = ./build

export PATH := $(PATH):$(abspath ./utils)

SIM_DIR   = ./test

V_FILE   = $(shell find ./build/ -name '*.v')
SIM_FILE = $(shell find ./test/ -name '*.cpp')
OBJ_DIR  = ./obj_dir

test:
	mill -i __.test

verilog:
	mkdir -p $(BUILD_DIR)
	mill -i __.test.runMain Elaborate -td $(BUILD_DIR)

help:
	mill -i __.test.runMain Elaborate --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

sim:
	verilator --cc --exe -trace -Wall --build -sv \
	$(SIM_FILE) $(V_FILE) \
	-I $(OBJ_DIR) \
	-j 0 --top-module Core \
       	--timescale-override 1ns/1us	

clean:
	-rm -rf $(BUILD_DIR) dump.vcd

.PHONY: test verilog help compile bsp reformat checkformat clean sim
