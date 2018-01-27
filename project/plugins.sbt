addSbtPlugin("org.foundweekends" % "sbt-bintray"           % "0.5.2")
addSbtPlugin("io.get-coursier"   % "sbt-coursier"          % "1.0.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"               % "0.9.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"            % "4.1.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager"   % "1.3.2")
addSbtPlugin("com.thesamet"      % "sbt-protoc"            % "0.99.13")
addSbtPlugin("com.lucidchart"    % "sbt-scalafmt-coursier" % "1.15")

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.7"
libraryDependencies += "org.slf4j"               % "slf4j-nop"      % "1.7.25"      // Needed by sbt-git
