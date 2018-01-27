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

package io.ontherocks.cbgrpc.client.greeter

import io.ontherocks.cbgrpc.client.InProcessSpec
import io.ontherocks.cbgrpc.protocol.greeter.GreeterGrpc.Greeter
import io.ontherocks.cbgrpc.protocol.greeter.{ GreeterGrpc, Greeting, ToBeGreeted }
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{ AsyncWordSpec, Matchers }

import scala.concurrent.Future

class GreeterClientSpec extends AsyncWordSpec with Matchers with InProcessSpec {

  private class TestGreeterService() extends Greeter {
    def sayHello(request: ToBeGreeted): Future[Greeting] =
      Future.successful(Greeting(s"Hi ${request.person.getOrElse("")}!"))
  }

  override protected def ssds = List(GreeterGrpc.bindService(new TestGreeterService(), global))

  "Greeter client" should {
    "return the greeting received from service" in withInProcessServerAndChannel {
      (server, channel) =>
        val client = new GreeterClient(channel)
        client.greet("Joe").runAsync.map { greeting =>
          greeting shouldEqual "Hi Joe!"
        }
    }
  }

}
