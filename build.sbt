name := "LuxoftExcercise"

version := "0.1"

scalaVersion := "2.13.7"
val akkaVersion = "2.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka"        %% "akka-stream"              % akkaVersion,
  "com.typesafe.akka"        %% "akka-stream-testkit"      % akkaVersion % Test,
  "com.typesafe.akka"        %% "akka-stream-typed"        % akkaVersion,
  //  "org.scalacheck"           %% "scalacheck"               % "1.14.0"    % Test,
  "com.novocode"             % "junit-interface"           % "0.11"      % Test
)
