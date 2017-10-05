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

package uk.gov.hmrc.feedbacksurveyfrontend

import play.api.Mode
import play.api.Play.{configuration, current}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val betaFeedbackUnauthenticatedUrl: String
  val reportAProblemPartialUrl: String
}

object FrontendAppConfig extends AppConfig with ServicesConfig {

  private lazy val contactFrontendBaseUrl = {
    if(current.mode==Mode.Dev) getConfString(s"contact-frontend.external-url", "")
    else ""
  }
  private val contactFrontendService = baseUrl("contact-frontend")
  private val contactFormServiceIdentifier = "FEEDBACK-SURVEY"

  override lazy val analyticsToken = configuration.getString(s"google-analytics.token").getOrElse("N/A")
  override lazy val analyticsHost = configuration.getString(s"google-analytics.host").getOrElse("service.gov.uk")
  override lazy val reportAProblemPartialUrl = s"$contactFrontendService/contact/problem_reports?secure=true"
  override lazy val betaFeedbackUnauthenticatedUrl = s"$contactFrontendBaseUrl/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"
}
