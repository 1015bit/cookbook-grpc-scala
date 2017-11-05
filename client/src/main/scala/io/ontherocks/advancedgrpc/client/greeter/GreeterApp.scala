/*
 * Copyright 2017 Petra Bierleutgeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ontherocks.advancedgrpc.client.greeter

import io.ontherocks.advancedgrpc.client.{ ClientConfiguration, DemoApp }
import org.apache.logging.log4j.scala.Logging
import pureconfig.error.ConfigReaderFailures

import scala.io.StdIn
import scala.util.{ Failure, Success }

object GreeterApp extends DemoApp with Logging {

  def main(args: Array[String]): Unit = {
    def handleConfigErrors(f: ConfigReaderFailures): Unit =
      logger.warn(s"Errors while loading config: ${f.toList}")

    def runDemo(config: ClientConfiguration): Unit = {
      val client     = new GreeterClient(channel(config))
      val personName = "Bob"
      logger.info(s"Calling `sayHello()` with name $personName...")
      import monix.execution.Scheduler.Implicits.global
      client
        .greet(personName)
        .runAsync
        .onComplete {
          case Success(greeting) => logger.info(s"Received greeting: $greeting")
          case Failure(t)        => logger.warn(s"Something went wrong: $t")
        }

      StdIn.readLine("Greeter client demo started. Press ENTER to exit..\n")
      ()
    }

    ClientConfiguration.load.fold(handleConfigErrors, runDemo)
  }

}
