package com.mrgueritte.atmostonce.dao

trait TweetDAO {

  def exist(tweetId: Long): Option[String]

  def setTwitterId(tweetId: Long): Option[Long]

  def incrTweetCount(userId: Long): Option[Long]

  def setMentionCount(userId: Long, mentionCount: Long): Option[Long]

  def setTweetLikeCount(userId: Long, likeCount: Long): Option[Long]

  def setScreenName(userId: Long, screenName: String): Boolean

  def setHashTagsCount(userId: Long, hashtagCount: Long): Option[Long]

}
