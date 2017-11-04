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

package io.ontherocks.advancedgrpc.client

import io.grpc.ManagedChannel
import io.ontherocks.advancedgrpc.protocol.greeter.{ GreeterGrpc, ToBeGreeted }
import monix.eval.Task
import org.apache.logging.log4j.scala.Logging
import pureconfig.error.ConfigReaderFailures

import scala.io.StdIn
import scala.util.{ Failure, Success }

object GreeterClient extends DemoClient with Logging {

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

class GreeterClient(channel: ManagedChannel) {

  val stub = GreeterGrpc.stub(channel)

  def greet(personName: String): Task[String] =
    Task.deferFutureAction { implicit scheduler =>
      stub.sayHello(ToBeGreeted(Some(personName))).map(_.message)
    }

}
