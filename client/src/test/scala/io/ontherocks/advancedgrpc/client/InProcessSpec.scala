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

package io.ontherocks.advancedgrpc
package client

import io.grpc.inprocess.{ InProcessChannelBuilder, InProcessServerBuilder }
import io.grpc.{ ManagedChannel, Server, ServerServiceDefinition }
import org.scalatest.Assertion

import scala.concurrent.Future
import scala.util.Try

/**
  * Makes it easy to start an in-process server for testing purposes.
  */
trait InProcessSpec {

  implicit class EnhancedInProcessServerBuilder(builder: InProcessServerBuilder) {
    def addServices(ssds: Seq[ServerServiceDefinition]): InProcessServerBuilder = {
      ssds.map(builder.addService(_))
      builder
    }
  }

  protected def ssds: Seq[ServerServiceDefinition]

  private def startServer(ssds: Seq[ServerServiceDefinition],
                          serverName: String): Either[Throwable, Server] =
    Try {
      InProcessServerBuilder
        .forName(serverName)
        .directExecutor()
        .addServices(ssds)
        .build()
        .start()
    }.toEither

  private def buildChannel(
      serverName: String
  ): Either[Throwable, ManagedChannel] =
    Try(
      InProcessChannelBuilder
        .forName(serverName)
        .directExecutor()
        .build()
    ).toEither

  protected def withInProcessServerAndChannel(
      testCode: (Server, ManagedChannel) => Future[Assertion]
  ): Future[Assertion] =
    withInProcessServerAndChannel("test-server", testCode)

  protected def withInProcessServerAndChannel(
      serverName: String,
      testCode: (Server, ManagedChannel) => Future[Assertion]
  ): Future[Assertion] = {
    val serverE  = startServer(ssds, serverName)
    val channelE = buildChannel(serverName)

    val resultE = for {
      server  <- serverE
      channel <- channelE
    } yield {
      testCode(server, channel)
    }

    serverE.map(_.shutdown())
    channelE.map(_.shutdown())

    resultE match {
      case Right(assertionF) => assertionF
      case Left(error)       => throw error
    }
  }

}
