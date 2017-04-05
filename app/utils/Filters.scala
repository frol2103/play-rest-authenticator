package utils

import javax.inject.Inject

import scala.concurrent.Future

import play.api._
import play.api.i18n.{I18nSupport,Messages,MessagesApi}
import play.api.mvc._, Results.Ok
import play.api.http.HttpFilters
import play.filters.csrf.CSRFFilter

import controllers.routes


class Filters @Inject() (
    csrfFilter:CSRFFilter
                        ) extends HttpFilters {

  override def filters = csrfFilter :: Nil
}
