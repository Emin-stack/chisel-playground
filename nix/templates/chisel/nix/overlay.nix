{self}: final: prev: {
  mill = let
    jre = final.jdk21;
  in
    (prev.mill.overrideAttrs {inherit jre;}).overrideAttrs
    (_: {passthru = {inherit jre;};});
}
