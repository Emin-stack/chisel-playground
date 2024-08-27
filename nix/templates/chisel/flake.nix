{
  description = "Chisel Nix Flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
  } @ inputs: let
    chisel-overlay = import ./nix/overlay.nix {inherit self;};
  in
    flake-utils.lib.eachDefaultSystem
    (
      system: let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [
            chisel-overlay
          ];
        };
        riscvPkgs = import nixpkgs {
          localSystem = "${system}";
          crossSystem = {
            config = "riscv64-unknown-linux-gnu";
            abi = "lp64";
          };
        };
        deps = with pkgs; [
          git
          gnumake

          verilator
          ccache

          circt
          llvm
          (mill.override {jre = pkgs.jdk21;})

          gtkwave
        ];
      in {
        legacyPackages = pkgs;
        formatter = pkgs.alejandra;
        devShells.default = pkgs.mkShell.override {stdenv = pkgs.clangStdenv;} {
          buildInputs = [deps riscvPkgs.buildPackages.gcc] ++ pkgs.lib.optional pkgs.stdenv.isLinux riscvPkgs.buildPackages.gdb;
          RV64_TOOLCHAIN_ROOT = "${riscvPkgs.buildPackages.gcc}";
          shellHook = ''
            export PATH=$RV64_TOOLCHAIN_ROOT/bin:$PATH
          '';
        };
      }
    )
    // {
      inherit inputs;
    };
}
