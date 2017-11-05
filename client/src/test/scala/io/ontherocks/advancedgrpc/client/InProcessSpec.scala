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

import io.grpc.inprocess.{ InProcessChannelBuilder, InProcessServerBuilder }
import io.grpc.{ ManagedChannel, Server, ServerServiceDefinition }
import org.scalatest.Assertion

import scala.concurrent.Future
import scala.util.Try

trait InProcessSpec {

  protected def ssd: ServerServiceDefinition

  private def startServer(ssd: ServerServiceDefinition,
                          serverName: String = "test-server"): Either[Throwable, Server] =
    Try(
      InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addService(ssd)
        .build()
        .start()
    ).toEither

  private def buildChannel(
      serverName: String = "test-server"
  ): Either[Throwable, ManagedChannel] =
    Try(
      InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build()
    ).toEither

  protected def withInProcessServerAndChannel(
      testCode: (Server, ManagedChannel) => Future[Assertion]
  ): Future[Assertion] = {
    val serverE  = startServer(ssd)
    val channelE = buildChannel()

    val result = for {
      server  <- serverE
      channel <- channelE
    } yield {
      testCode(server, channel)
    }

    result match {
      case Right(assertion) => assertion
      case Left(error) =>
        serverE.map(_.shutdown())
        channelE.map(_.shutdown())
        throw error
    }
  }

}
