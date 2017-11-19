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

package io.ontherocks.advancedgrpc.service

import io.ontherocks.advancedgrpc.protocol.greeter.GreeterGrpc
import io.ontherocks.advancedgrpc.service.greeter.GreeterService

import io.grpc.Server
import monix.execution.Scheduler.Implicits.{ global => scheduler }
import pureconfig.error.ConfigReaderFailures

object Main extends App with DemoServer {

  def handleConfigErrors(f: ConfigReaderFailures): Unit =
    logger.warn(s"Errors while loading config: ${f.toList}")

  /**
    * Cleanup resources when JVM terminates.
    */
  def registerShutdownHook(server: Server): Unit =
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        logger.info("Shutting down server. Bye.")
        server.shutdown()
        ()
      }
    })

  def runServer(config: ServiceConfiguration): Unit = {
    val ssd    = GreeterGrpc.bindService(new GreeterService(), scheduler)
    val server = start(config, ssd)

    logger.info("Registering shutdown hook.")
    registerShutdownHook(server)

    server.awaitTermination()
  }

  ServiceConfiguration.load.fold(
    handleConfigErrors,
    runServer
  )

}
