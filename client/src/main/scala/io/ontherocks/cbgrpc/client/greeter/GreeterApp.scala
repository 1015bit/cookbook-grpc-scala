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

package io.ontherocks.cbgrpc.client
package greeter

import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit
import org.apache.logging.log4j.scala.Logging
import pureconfig.error.ConfigReaderFailures

import scala.util.{ Failure, Success }

object GreeterApp extends App with DemoApp with Logging {

  def handleConfigErrors(f: ConfigReaderFailures): Unit =
    logger.warn(s"Errors while loading config: ${f.toList}")

  /**
    * Cleanup resources when JVM terminates.
    */
  def registerShutdownHook(channel: ManagedChannel): Unit =
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        logger.info("Shutting down channel. Bye.")
        channel.shutdown()
        ()
      }
    })

  def runDemo(config: ClientConfiguration): Unit = {
    val channel    = managedChannel(config)
    val client     = new GreeterClient(channel)
    val personName = "Bob"

    logger.info("Registering shutdown hook.")
    registerShutdownHook(channel)

    logger.info(s"Calling `sayHello()` with name $personName...")
    import monix.execution.Scheduler.Implicits.global
    client
      .greet(personName)
      .runAsync
      .onComplete {
        case Success(greeting) =>
          logger.info(s"Received greeting: $greeting")
          channel.shutdown()
        case Failure(t) =>
          logger.warn(s"Something went wrong: $t")
      }

    channel.awaitTermination(60, TimeUnit.SECONDS)
    ()
  }

  ClientConfiguration.load.fold(handleConfigErrors, runDemo)

}
