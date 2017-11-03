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

import io.grpc.{ Server, ServerBuilder, ServerServiceDefinition }

import scala.io.StdIn

trait DemoServer {

  def runServer(ssd: ServerServiceDefinition): Server = {
    val server = ServerBuilder
      .forPort(50051)
      .addService(ssd)
      .build
      .start

    // make sure our server is stopped when jvm is shut down
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        server.shutdown()
        ()
      }
    })

    println("Demo server started. Press ENTER to shutdown...")
    StdIn.readLine()

    server.shutdown()
  }

}
