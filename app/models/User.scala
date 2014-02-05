package models

import scala.xml.XML._
import scala.xml.Utility.escape
import java.util.UUID

case class LoginUser(email: String, password: String)

case class RegisterUser(email: String, emailConfirm: String, password: String, passwordConfirm: String)

class User(email: String, password: String, id: String = UUID.randomUUID().toString, token: Option[String] = None) {
  def toXml = {
    <user>
      <email>{escape(email)}</email>
      <password>{escape(password)}</password>
      <id>{id}</id>
      <token>{token.getOrElse("")}</token>
    </user>
  }

  def getId = id
  def getEmail = email
  def getPassword = password
  def getToken = token
  def hasToken = token.get != None && token.get != ""
}

object User {
  val FILENAME = "app/models/users.xml"

  def getUsers : List[User]= {
    (loadFile(FILENAME) \\ "user").map(fromXml _).toList
  }

  def getUserById(id : String) : Option[User] = {
    getUsers.find(_.getId == id)
  }

  def getUserByEmail(email: String) : Option[User] = {
    getUsers.find(_.getEmail == email)
  }

  def saveUser(user: User) {
    saveUsers(user :: getUsers)
  }

  def saveUsers(users: List[User]) {
    save(FILENAME,
      <users>
        {users.map(_.toXml)}
      </users>
    )
  }

  def createUser(u: LoginUser) : User = {
    val user = new User(u.email, u.password)
    saveUser(user)
    user
  }

  def updateToken(id: String, newToken: Option[String]) {
    saveUsers(getUsers.map { u =>
        if(u.getId == id) {
          new User(u.getEmail, u.getPassword, u.getId, newToken)
        } else {
          u
        }
      }
    )
  }

  def fromXml(node: scala.xml.Node) : User = {
    new User(
      (node \ "email").text,
      (node \ "password").text,
      (node \ "id").text,
      Option((node \ "token").text)
    )
  }
}

