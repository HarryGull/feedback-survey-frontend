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

package uk.gov.hmrc.feedbacksurveyfrontend.services

import controllers.bindable.Origin
import play.api.Play.current

import scala.collection.JavaConversions._

case class OriginConfigItem(token: Option[String], customFeedbackUrl: Option[String])

class OriginService {

  lazy val originConfigItems: List[OriginConfigItem] = current.configuration.getConfigList("origin-services").map(_.toList).getOrElse(Nil).map { configItem =>
    OriginConfigItem(configItem.getString("token"), configItem.getString("customFeedbackUrl"))
  }

  def isValid(origin: Origin): Boolean = !originConfigItems.filter(o => o.token.equals(Some(origin.value))).isEmpty

  def customFeedbackUrl(origin: Origin): Option[String] = originConfigItems.filter(o => o.token.equals(Some(origin.value))).headOption.flatMap(_.customFeedbackUrl)
}
