/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.feedbacksurveyfrontend.bindable

import controllers.bindable.{Binders, Origin}
import uk.gov.hmrc.feedbacksurveyfrontend.services.{OriginConfigItem, OriginService}
import utils.UnitTestTraits

class BindableSpec extends UnitTestTraits {

    
  object testBindable extends Binders {
    val originService = new OriginService {
      override lazy val originConfigItems = List(
        OriginConfigItem(Some("PERTAX"), None)
      )
      
    }
  }

  "Calling originBinder.unbind" should {

    "return origin=PERTAX when key is origin and value is PERTAX" in {
      testBindable.originBinder.unbind("origin", Origin("PERTAX")) shouldBe "origin=PERTAX"
    }
    "UrlEncode keys" in {
      testBindable.originBinder.unbind("£", Origin("PERTAX")) shouldBe "%C2%A3=PERTAX"
    }
    "UrlEncode values" in {
      testBindable.originBinder.unbind("origin", Origin("£")) shouldBe "origin=%C2%A3"
    }
  }

  "Calling originBinder.bind" should {

    "return an origin when called with a valid string" in {
      testBindable.originBinder.bind("origin", Map("origin" -> Seq("PERTAX"))) shouldBe Some(Right(Origin("PERTAX")))
    }
    "return error message when called with an empty string" in {
      testBindable.originBinder.bind("origin", Map("origin" -> Seq(""))) shouldBe Some(Left("Invalid origin in queryString"))
    }
    "return error message when called with an invalid string" in {
      testBindable.originBinder.bind("origin", Map("origin" -> Seq("INVALID"))) shouldBe Some(Left("Invalid origin in queryString"))
    }
    "return None when called with an empty map" in {
      testBindable.originBinder.bind("origin", Map()) shouldBe None
    }
  }
}
