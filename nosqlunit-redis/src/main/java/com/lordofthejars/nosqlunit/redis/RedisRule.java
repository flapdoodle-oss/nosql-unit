package com.lordofthejars.nosqlunit.redis;

import static com.lordofthejars.nosqlunit.redis.ManagedRedisConfigurationBuilder.newManagedRedisConfiguration;
import static com.lordofthejars.nosqlunit.redis.EmbeddedRedisConfigurationBuilder.newEmbeddedRedisConfiguration;
import redis.clients.jedis.BinaryJedisCommands;

import com.lordofthejars.nosqlunit.core.AbstractNoSqlTestRule;
import com.lordofthejars.nosqlunit.core.DatabaseOperation;

public class RedisRule extends AbstractNoSqlTestRule {

	private static final String EXTENSION = "json";
	
	private DatabaseOperation<? extends BinaryJedisCommands> databaseOperation;
	
	public static class RedisRuleBuilder {
		
		private AbstractRedisConfiguration redisConfiguration;
		private Object target;
		
		private RedisRuleBuilder() {
			super();
		}
		
		public static RedisRuleBuilder newRedisRule() {
			return new RedisRuleBuilder();
		}
		
		public RedisRuleBuilder configure(AbstractRedisConfiguration redisConfiguration) {
			this.redisConfiguration = redisConfiguration;
			return this;
		}
		
		public RedisRuleBuilder unitInstance(Object target) {
			this.target = target;
			return this;
		}
		
		public RedisRule defaultManagedRedis() {
			return new RedisRule(newManagedRedisConfiguration().build());
		}
		
		public RedisRule defaultManagedRedis(Object target) {
			return new RedisRule(newManagedRedisConfiguration().build(), target);
		}
		
		public RedisRule defaultEmbeddedRedis() {
			return new RedisRule(newEmbeddedRedisConfiguration().build());
		}
		
		public RedisRule defaultEmbeddedRedis(Object target) {
			return new RedisRule(newEmbeddedRedisConfiguration().build(), target);
		}
		
		public RedisRule build() {

			if(this.redisConfiguration == null) {
				throw new IllegalArgumentException("Configuration object should be provided.");
			}
			
			return new RedisRule(redisConfiguration, target);
		}
		
	}
	
	public RedisRule(AbstractRedisConfiguration redisConfiguration) {
		super(redisConfiguration.getConnectionIdentifier());
		
		this.databaseOperation = redisConfiguration.getDatabaseOperation();
	}
	
	/*With JUnit 10 is impossible to get target from a Rule, it seems that future versions will support it. For now constructor is apporach is the only way.*/
	public RedisRule(AbstractRedisConfiguration redisConfiguration, Object target) {
		this(redisConfiguration);
		setTarget(target);
	}
	
	@Override
	public DatabaseOperation<? extends BinaryJedisCommands> getDatabaseOperation() {
		return databaseOperation;
	}

	@Override
	public String getWorkingExtension() {
		return EXTENSION;
	}

}
