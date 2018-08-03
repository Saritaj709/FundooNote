package com.bridgelabz.fundonotes.user.repository;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepositoryImpl implements RedisRepository{

	@Value("${Key}")
	private String KEY;
			
	private RedisTemplate<String, String> redisTemplate;
	private HashOperations<String,String,String> hashOperations;
	
	@Autowired
	RedisRepositoryImpl(RedisTemplate<String,String> redisTemplate){
		this.redisTemplate=redisTemplate;
	}
	@PostConstruct
	private void init() {
		hashOperations=redisTemplate.opsForHash();
	}
	@Override
	public void saveInRedis(String randomString,String userId) {
     
		hashOperations.put(KEY,randomString, userId);
		
	}

	@Override
	public String getFromRedis(String uuid) {
		return hashOperations.get(KEY, uuid);
	}

	@Override
	public void deleteFromRedis(String userId) {
		hashOperations.delete(KEY, userId);
	}

}
