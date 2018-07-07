addSbtPlugin("org.foundweekends" % "sbt-bintray"           % "0.5.4")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"               % "1.0.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header"            % "5.0.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager"   % "1.3.5")
addSbtPlugin("com.thesamet"      % "sbt-protoc"            % "0.99.18")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"          % "1.5.1")

libraryDependencies += "com.thesamet.scalapb"   %% "compilerplugin" % "0.7.4"
libraryDependencies += "org.slf4j"               % "slf4j-nop"      % "1.7.25"      // Needed by sbt-git
