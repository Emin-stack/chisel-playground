{
  stdenv,
  lib,
  gnumake,
  verilator,
  binutils,
  zlib,
}: let
  self = stdenv.mkDerivation rec {
    name = "verilator-timing";

    src = with lib.fileset;
      toSource {
        root = ./../playground/test/src;
        fileset = unions [
          ./../playground/test/src/examples
        ];
      };

    nativeBuildInputs = [
      gnumake
      verilator
      binutils
      zlib
    ];

    buildPhase = ''
      verilator --cc -trace-fst --exe -Wall --timing -O1 -sv \
        examples/top.sv examples/tb.sv examples/main.cc \
        --top-module tb --CFLAGS "-DTRACE=1"

      cd obj_dir && make -f Vtb.mk
    '';

    installPhase = ''
      mkdir -p $out/bin $out/include $out/lib $out/obj_dir
      cp *.h $out/include
      cp *.a $out/lib
      cp *.o $out/obj_dir
      cp Vtb $out/bin
    '';

    meta.description = "Verilog testbench for verilator";
  };
in
  self
