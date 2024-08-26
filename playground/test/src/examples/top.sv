`timescale 1ns/1ns
//模块
module top(
  input        a,
  input        b,
  output       co
);
  assign co  = a && b;
endmodule
