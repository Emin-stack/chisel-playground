`timescale 1ns/1ns
module tb();
  reg        a;
  reg        b;
  wire       co;
 top top_0(
    .a   (a),
    .b   (b),
    .co  (co)
);
  initial begin
    a   = 1'b0;
    b   = 1'b0;
  end
  initial begin//a
    #1  a = 1'b1;
    #99 a = 1'b0;
  end
  initial begin//b
    #10 b = 1'b0;
    #1  b = 1'b1;
  end
  initial begin//finish
		$display("start: co:%d\n",co);
    #110 $finish;
		$display("end: co:%d\n",co);
  end
endmodule
