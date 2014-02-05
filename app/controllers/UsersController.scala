package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import models._

object UsersController extends Controller {

  val CurrentUserKey = "current.user"

  def profile = Action { implicit request =>
    val currentUser = request.session.get(CurrentUserKey)
    if (currentUser != None && User.getUserById(currentUser.get) != None) {
      val user = User.getUserById(currentUser.get).get
      if(user.hasToken) {
        Ok(views.html.profile2(user, FoursquareAuth.getCheckinInfo(user.getToken.get), true))
      } else {
        Ok(views.html.profile(user, FoursquareAuth.getAuthUrl, true))
      }
    } else {
      Ok(views.html.login())
    }
  }

  def profiles(userid: String) = Action { implicit request =>
    val currentUser = request.session.get(CurrentUserKey)
    if(currentUser != None && currentUser.get == userid) {
      Redirect(routes.UsersController.profile())
    } else {
      val user = User.getUserById(userid).get
      if(user.hasToken) {
        Ok(views.html.profile2(user, FoursquareAuth.getCheckinInfo(user.getToken.get), false))
      } else {
        Ok(views.html.profile(user, FoursquareAuth.getAuthUrl, false))
      }
    }
  }

  def login = Action { implicit request =>
    Ok(views.html.login())
  }

  def register = Action { implicit request =>
      Ok(views.html.register())
  }

  def logout = Action { implicit request =>
    Redirect(routes.UsersController.login()).withSession(request.session - CurrentUserKey)
  }

  def allUsers = Action { implicit request =>
    Ok(views.html.users(User.getUsers))
  }

  def getCurrentUser(request: Request[AnyContent]) : Option[User] = {
    val currentUser = request.session.get(CurrentUserKey)
    if(currentUser != None) {
      User.getUserById(currentUser.get)
    } else {
      None
    }
  }

  val loginForm : Form[LoginUser] = Form(
    mapping(
      "email" -> text.verifying(nonEmpty),
      "password" -> text.verifying(nonEmpty)
    )(LoginUser.apply)(LoginUser.unapply)
  )

  val registerForm : Form[RegisterUser] = Form(
    mapping(
      "email" -> text.verifying(nonEmpty),
      "emailConfirm" -> text.verifying(nonEmpty),
      "password" -> text.verifying(nonEmpty),
      "passwordConfirm" -> text.verifying(nonEmpty)
    )(RegisterUser.apply)(RegisterUser.unapply)
  )

  def loginSubmit = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => Redirect(routes.UsersController.login()).flashing("message" -> "username/password error"),
      data => {
        val user = User.getUserByEmail(data.email).getOrElse(User.createUser(data))
        Redirect(routes.UsersController.profile()).withSession(CurrentUserKey -> user.getId)
      }
    )
  }

  def registerSubmit = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      errors => Redirect(routes.UsersController.register()).flashing("message" -> "email or password error"),
      data => {
        if(data.email != data.emailConfirm || data.password != data.passwordConfirm) {
          Redirect(routes.UsersController.register()).flashing("message" -> "email or password do not match")
        }
        val user = User.getUserByEmail(data.email).getOrElse(User.createUser(LoginUser(data.email, data.password)))
        Redirect(routes.UsersController.profile()).withSession(CurrentUserKey -> user.getId)
      }
    )
  }


}
