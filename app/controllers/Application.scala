package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.validation.Constraints._

import models._

object Application extends Controller {

  def index = Action {
    Redirect(routes.UsersController.login())
  }

}