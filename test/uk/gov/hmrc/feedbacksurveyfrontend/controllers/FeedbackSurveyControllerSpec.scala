/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.feedbacksurveyfrontend.controllers

import akka.stream.ActorMaterializer
import controllers.FeedbackSurveyController
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._
import utils.UnitTestTraits
import akka.actor.ActorSystem
import play.api.mvc.AnyContentAsEmpty

import scala.concurrent.ExecutionContext


class FeedbackSurveyControllerSpec extends UnitTestTraits {

  val request = FakeRequest()
  implicit val as = ActorSystem()
  implicit val mat = ActorMaterializer()
  object TestFeedbackSurveyController extends FeedbackSurveyController

  "FeedbackSurveyController" should {

    "Go to the ableToDo page " in {
      val result = TestFeedbackSurveyController.ableToDo.apply((FakeRequest("GET", "/ableToDo")))
      status(result) shouldBe OK
    }

    "redirect to the usingService page " in {
      val result = TestFeedbackSurveyController.ableToDoContinue.apply(FakeRequest()).run()
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should include("/feedback-survey/usingService")
    }

    "Go to the usingService page " in {
      val result = TestFeedbackSurveyController.usingService.apply((FakeRequest("GET", "/usingService")))
      status(result) shouldBe OK
    }

    "redirect to the aboutService page " in {
      val result = TestFeedbackSurveyController.usingServiceContinue.apply(FakeRequest()).run()
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should include("/feedback-survey/aboutService")
    }

    "Go to the recommendService page " in {
      val result = TestFeedbackSurveyController.recommendService.apply((FakeRequest("GET", "/usingService")))
      status(result) shouldBe OK
    }

    "redirect to the Thank you page " in {
      val result = TestFeedbackSurveyController.recommendServiceContinue.apply(FakeRequest()).run()
      status(result) shouldBe SEE_OTHER
      redirectLocation(result).get should include("/feedback-survey/thankYou")
    }

    "Go to the Thank you page " in {
      val result = TestFeedbackSurveyController.recommendService.apply((FakeRequest("GET", "/usingService")))
      status(result) shouldBe OK
    }
  }
}