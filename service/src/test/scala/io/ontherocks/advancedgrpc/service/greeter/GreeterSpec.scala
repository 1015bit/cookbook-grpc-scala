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

import io.ontherocks.advancedgrpc.protocol.greeter.ToBeGreeted
import org.scalatest.{ AsyncWordSpec, Matchers }

class GreeterSpec extends AsyncWordSpec with Matchers {

  val greeterService = new GreeterService()

  "Greeter service" should {
    "return a greeting for the given person" in {
      val personName = "Bob"
      val result     = greeterService.sayHello(ToBeGreeted(Some(personName)))
      result.map { greeting =>
        greeting.message shouldEqual s"Hello $personName!"
      }
    }
  }

}
