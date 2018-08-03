package com.bridgelabz.fundonotes.user.repository;

public interface RedisRepository {

	public void saveInRedis(String UUID,String userId);

	public String getFromRedis(String UUID);

	public void deleteFromRedis(String userId);
}
