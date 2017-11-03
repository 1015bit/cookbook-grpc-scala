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

package io.ontherocks.advancedgrpc.service.greeter

import io.ontherocks.advancedgrpc.protocol.greeter.GreeterGrpc
import io.ontherocks.advancedgrpc.service.{ DemoServer, ServiceConfiguration }
import monix.execution.Scheduler.Implicits.{ global => scheduler }
import pureconfig.error.ConfigReaderFailures

object GreeterServer extends DemoServer {

  def main(args: Array[String]): Unit = {
    def handleConfigErrors(f: ConfigReaderFailures): Unit =
      logger.warn(s"Errors while loading config: ${f.toList}")

    def runServer(config: ServiceConfiguration): Unit = {
      val ssd = GreeterGrpc.bindService(new GreeterService(), scheduler)
      start(config, ssd)
      ()
    }

    ServiceConfiguration.load.fold(
      handleConfigErrors,
      runServer
    )
  }

}
