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

package controllers

import controllers.bindable.Origin
import models.feedbackSurveyModels._
import play.api.Play
import uk.gov.hmrc.play.frontend.controller.FrontendController
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import uk.gov.hmrc.feedbacksurveyfrontend.LocalTemplateRenderer
import uk.gov.hmrc.renderer.TemplateRenderer
import uk.gov.hmrc.feedbacksurveyfrontend.services.OriginService
import utils.LoggingUtils
import utils.FeedbackSurveySessionKeys._
import uk.gov.hmrc.feedbacksurveyfrontend.FrontendAppConfig
import uk.gov.hmrc.feedbacksurveyfrontend.views.html


object FeedbackSurveyController extends FeedbackSurveyController {
  val originService = new OriginService
}

trait FeedbackSurveyController extends FrontendController with LoggingUtils with I18nSupport {

  implicit val messagesApi: MessagesApi = Play.current.injector.instanceOf[MessagesApi]
  implicit val templateRenderer: TemplateRenderer = LocalTemplateRenderer

  def originService: OriginService

  val ableToDo  = Action { implicit request =>
    Ok(html.feedbackSurvey.ableToDo(formMappings.ableToDoForm))
  }

  val ableToDoContinue =  Action (parse.form(formMappings.ableToDoForm)) { implicit request =>
        val ableToDoWhatNeeded = request.body.ableToDoWhatNeeded
    audit("feedback-survey", Map("origin" -> getOriginFromSession.value,
      "ableToDoWhatNeeded" -> ableToDoWhatNeeded.getOrElse("")), eventTypeSuccess)
    Redirect(routes.FeedbackSurveyController.usingService)
  }

  val usingService =  Action { implicit request =>
    Ok(html.feedbackSurvey.usingService(formMappings.usingServiceForm))
  }

  val usingServiceContinue = Action (parse.form(formMappings.usingServiceForm)) { implicit request =>
    val beforeUsingThisService = request.body.beforeUsingThisService
    var option0, option1, option2, option3, option4, option5, option6: (String,String) = ("","")
    if (beforeUsingThisService.lift(0).isDefined) {option0 = beforeUsingThisService.lift(0).get -> "Checked"}
    if (beforeUsingThisService.lift(1).isDefined) {option1 = beforeUsingThisService.lift(1).get -> "Checked"}
    if (beforeUsingThisService.lift(2).isDefined) {option2 = beforeUsingThisService.lift(2).get -> "Checked"}
    if (beforeUsingThisService.lift(3).isDefined) {option3 = beforeUsingThisService.lift(3).get -> "Checked"}
    if (beforeUsingThisService.lift(4).isDefined) {option4 = beforeUsingThisService.lift(4).get -> "Checked"}
    if (beforeUsingThisService.lift(5).isDefined) {option5 = beforeUsingThisService.lift(5).get -> "Checked"}
    if (beforeUsingThisService.lift(6).isDefined) {option6 = beforeUsingThisService.lift(6).get -> "Checked"}
    audit("feedback-survey", Map(
      "origin" -> getOriginFromSession.value,
      option0, option1, option2, option3, option4, option5, option6
    ).filter((t) => t._1 != ""), eventTypeSuccess)
    Redirect(routes.FeedbackSurveyController.aboutService)
  }

  val aboutService = Action { implicit request =>
    Ok(html.feedbackSurvey.aboutService(formMappings.aboutServiceForm))
  }

  val aboutServiceContinue =  Action (parse.form(formMappings.aboutServiceForm)) { implicit request =>
    val serviceReceived = request.body.serviceReceived
    audit("feedback-survey", Map("origin" -> getOriginFromSession.value,
      "serviceReceived" -> serviceReceived.getOrElse("")), eventTypeSuccess)
    Redirect(routes.FeedbackSurveyController.recommendService)
  }

  val recommendService = Action { implicit request =>
    Ok(html.feedbackSurvey.recommendService(formMappings.recommendServiceForm))
  }

  val recommendServiceContinue =  Action (parse.form(formMappings.recommendServiceForm)) { implicit request =>
    val reasonForRating = request.body.reasonForRating
    val recommendRating = request.body.recommendRating
    audit("feedback-survey", Map(
      "origin" -> getOriginFromSession.value,
      "reasonForRating" -> reasonForRating.getOrElse(""),
      "recommendRating" -> recommendRating.getOrElse("")), eventTypeSuccess)

    originService.customFeedbackUrl(getOriginFromSession(request)) match {
      case Some(x) => Redirect(x)
      case None => Redirect(routes.FeedbackSurveyController.thankYou(getOriginFromSession))
    }
  }

  def thankYou(origin : Origin): Action[AnyContent] = Action {
    implicit request =>
      if(originService.isValid(Origin(origin.value))) {
        Ok(html.feedbackSurvey.thankYou(FrontendAppConfig.urLinkUrl))
      } else {
        Ok(html.error_template("global_errors.title", "global_errors.heading", "global_errors.message"))
      }
  }

  def getOriginFromSession(implicit request: Request[_]): Origin = {
    Origin(request.session.get(sessionOriginService).get)
  }
}
