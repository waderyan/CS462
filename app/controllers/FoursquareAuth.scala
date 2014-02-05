package controllers

import play.api.mvc._

import org.scribe.builder._
import org.scribe.builder.api.Foursquare2Api
import org.scribe.model.{Token, Verb, OAuthRequest, Verifier}

import models._

object FoursquareAuth extends Controller {

  val ClientKey = "WBDINX4MMKPJRJ5JDR0AZKFBSOJ1CIC31K4ZK2B3JXWRW5MQ"
  val ClientSecret = "SEYXQENH5YQKT5ETKT5ONV3RC4TTVTYJA44XMO3SIN4A50ZG"
  val RegisteredRedirect = "https://localhost:9051/callback"

  val Version = "v=20140121&oauth_token="
  val CheckinUrl = "https://api.foursquare.com/v2/users/self/checkins?" + Version
  val ResourceUrl = "https://api.foursquare.com/v2/users/self?" + Version

  var EmptyToken : Token = null

  def getService = {
    new ServiceBuilder()
      .provider(classOf[Foursquare2Api])
      .apiKey(ClientKey)
      .apiSecret(ClientSecret)
      .callback(RegisteredRedirect)
      .build
  }

  def getAuthUrl : String = {
    getService.getAuthorizationUrl(null)
  }

  def isConnected(user: User) = user.getToken match {
    case None => false
    case _ => {
      val service = getService
      val request = new OAuthRequest(Verb.GET, CheckinUrl + user.getToken.get)
      service.signRequest(new Token(user.getToken.get, ClientSecret), request)
      val response = request.send
      if(response.getCode != 200) {
        User.updateToken(user.getId, None)
      }
      response.getCode == 200
    }
  }

  def getCheckinInfo(token: String, limit:Int = 250) = {
    val service = getService
    val request = new OAuthRequest(Verb.GET, CheckinUrl + token + "&limit=" + limit.toString)
    service.signRequest(new Token(token, ClientSecret), request)
    val response = request.send
    response.getBody
  }

  def connect = Action { implicit request =>
    NotImplemented
  }

  def callback = Action { implicit request =>
    val user = UsersController.getCurrentUser(request).getOrElse(null)
    if (user == null) {
      // this should never happen
      Redirect(routes.UsersController.login()).flashing("message" -> "no user logged in")
    }
    val accessToken = getService.getAccessToken(null, new Verifier(request.getQueryString("code").get))
    User.updateToken(user.getId, Some(accessToken.getToken))

    Redirect(routes.UsersController.profile())
  }

}
