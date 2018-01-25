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

package views.feedbackSurvey

import controllers.FeedbackSurveyController
import controllers.bindable.Origin
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.feedbacksurveyfrontend.services.{OriginConfigItem, OriginService}
import utils.{HtmlUtils, UnitTestTraits}
import uk.gov.hmrc.feedbacksurveyfrontend.utils.MockTemplateRenderer
import uk.gov.hmrc.renderer.TemplateRenderer

class pageTests extends UnitTestTraits with HtmlUtils {
  val lookupFailure = Json.parse( """{"reason": "Generic test reason"}""")

  def testRequest(page: String): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, "/feedback-survey/" + s"$page")

  object TestLookupController extends FeedbackSurveyController {

    override implicit val templateRenderer: TemplateRenderer = MockTemplateRenderer

    val originService = new OriginService {
      override lazy val originConfigItems = List(
        OriginConfigItem(Some("AWRS"), None)
      )
    }
  }

  "FeedbackSurvey Controller" should {

    "render ableToDo page correctly" in {
      val document: Document = TestLookupController.ableToDo.apply(testRequest(page = "ableToDo"))
      document.getElementById("intro").text shouldBe Messages("feedbackSurvey.page1.para1")
      document.getElementById("ableToDoWhatNeeded_legend").text should include(Messages("feedbackSurvey.page1.question1"))
      document.getElementById("ableToDoWhatNeeded-yes").text shouldBe ""
      document.getElementById("ableToDoWhatNeeded-no").text shouldBe ""
      document.getElementById("save-and-continue").text shouldBe Messages("generic.continue")
    }

    "render usingService page correctly" in {
      val document: Document = TestLookupController.usingService.apply(testRequest(page = "usingService"))
      document.getElementById("beforeUsingThisService").text shouldBe Messages("feedbackSurvey.page2.question1")
      document.getElementById("save-and-continue").text shouldBe Messages("generic.continue")
    }

    "render aboutService page correctly" in {
      val document: Document = TestLookupController.aboutService.apply(testRequest(page = "aboutService"))
      document.getElementById("serviceReceived").text shouldBe Messages("feedbackSurvey.page3.question1")
      document.getElementById("save-and-continue").text shouldBe Messages("generic.continue")
    }

    "render recommendService page correctly" in {
      val document: Document = TestLookupController.recommendService.apply(testRequest(page = "recommendService"))
      document.getElementById("reasonForRatingHeader").text shouldBe Messages("feedbackSurvey.page4.question2")
      document.getElementById("save-and-continue").text shouldBe Messages("generic.continue")
    }

    "render thankYou page correctly with valid origin" in {
      val document: Document = TestLookupController.thankYou(Origin("AWRS")).apply(testRequest(page = "thankYou"))
      document.getElementById("thankYou").text shouldBe Messages("feedbackSurvey.page5.title")
    }

    "render error page correctly with invalid origin" in {
      val document: Document = TestLookupController.thankYou(Origin("INVALID_ORIGIN")).apply(testRequest(page = "thankYou"))
      document.body.getElementsByClass("heading-large").text should include("Service unavailable")
    }

  }
}
