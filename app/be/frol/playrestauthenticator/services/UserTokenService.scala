package be.frol.playrestauthenticator.services

import java.util.UUID
import javax.inject._

import be.frol.playrestauthenticator.daos.UserTokenDao
import be.frol.playrestauthenticator.models.UserToken

class UserTokenService @Inject() (userTokenDao:UserTokenDao) {
  def find(id:UUID) = userTokenDao.find(id)
  def save(token:UserToken) = userTokenDao.save(token)
  def remove(id:UUID) = userTokenDao.remove(id)
}
