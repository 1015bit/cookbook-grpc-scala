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

import io.ontherocks.advancedgrpc.protocol.greeter.GreeterGrpc.Greeter
import io.ontherocks.advancedgrpc.protocol.greeter.{ GreeterGrpc, Greeting, ToBeGreeted }

import monix.execution.Scheduler.Implicits.{ global => scheduler }

import scala.concurrent.Future

object GreeterServer extends DemoServer {

  class GreeterService extends Greeter {
    def sayHello(request: ToBeGreeted): Future[Greeting] = {
      val person = request.person.getOrElse("anonymous")
      Future.successful(Greeting(s"Hello $person!"))
    }
  }

  def main(args: Array[String]): Unit = {
    val ssd = GreeterGrpc.bindService(new GreeterService(), scheduler)
    runServer(ssd)
    ()
  }

}
