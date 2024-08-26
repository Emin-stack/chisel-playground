{
  description = "Chisel Playground Flake";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    nixpkgs-old.url = "github:NixOS/nixpkgs/336eda0d07dc5e2be1f923990ad9fdb6bc8e28e3";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
    nixpkgs-old,
  } @ inputs: let
    playground-overlay = import ./nix/overlay.nix {inherit self;};
  in
    flake-utils.lib.eachDefaultSystem
    (
      system: let
        pkgs = import nixpkgs {
          inherit system;
          config.packageOverrides = pkgs: {
            graalvm = nixpkgs-old.legacyPackages.${system}.graalvm-ce;
          };
          overlays = [
            playground-overlay
          ];
        };
        deps = with pkgs; [
          git
          gnumake

          verilator
          ccache

          circt
          llvm
          (mill.override {jre = pkgs.graalvm;})

          gtkwave
        ];
      in {
        legacyPackages = pkgs;
        formatter = pkgs.alejandra;
        devShells.default = pkgs.mkShell.override {stdenv = pkgs.clangStdenv;} {
          buildInputs = [deps];
          shellHook = ''
          '';
        };
      }
    )
    // {inherit inputs;};
}
