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

package controllers.bindable

import controllers.bindable.Binders.originService
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.feedbacksurveyfrontend.services.OriginService


object Binders extends Binders {

  val originService = new OriginService
}


trait Binders {

  implicit def originBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Origin] {

    val enc = play.utils.UriEncoding.encodePathSegment(_: String, "UTF-8")

    override def unbind(key: String, origin: Origin): String = enc(key) + "=" + enc(origin.value)

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Origin]] = {
      params.get(key) match {
        case None =>
          None
        case Some(Seq(originString)) if(originService.isValid(Origin(originString))) =>
          Some(Right(Origin(originString)))
        case _ =>
          Some(Left("Invalid origin in queryString"))
      }
    }
  }
}
