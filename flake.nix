{
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";
    utils.url = "github:numtide/flake-utils";
  };
  outputs =
    inputs@{
      self,
      ...
    }:
    inputs.utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = inputs.nixpkgs.legacyPackages.${system};
        jdk21 = pkgs.temurin-bin-21;
        jdk17 = pkgs.temurin-bin-17;
      in
      {
        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            jdt-language-server
          ];

          JAVA_HOME = "${jdk21.home}";

          JDK_21 = "${jdk21.home}";
          JDK_17 = "${jdk17.home}";
        };
      }
    );
}
