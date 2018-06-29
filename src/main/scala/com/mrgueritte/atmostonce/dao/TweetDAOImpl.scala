package com.mrgueritte.atmostonce.dao

import com.redis.RedisClient

class TweetDAOImpl(client: RedisClient) extends TweetDAO {

  override def exist(tweetId: Long): Option[String] =
    client.get[String](s"twid.$tweetId")

  override def setTwitterId(tweetId: Long): Option[Long] =
    client.incr(s"twid.$tweetId")

  override def incrTweetCount(userId: Long): Option[Long] =
    client.incr(s"tw.$userId.count")

  override def setMentionCount(userId: Long, mentionCount: Long): Option[Long] =
    client.incrby(s"tw.$userId.mentions", mentionCount)

  override def setTweetLikeCount(userId: Long, likeCount: Long): Option[Long] =
    client.incrby(s"tw.$userId.fav", likeCount)

  override def setScreenName(userId: Long, screenName: String): Boolean =
    client.set(s"tw.$userId.name", screenName)

  override def setHashTagsCount(userId: Long, hashtagCount: Long): Option[Long] =
    client.incrby(s"tw.$userId.hashtags", hashtagCount)

}
