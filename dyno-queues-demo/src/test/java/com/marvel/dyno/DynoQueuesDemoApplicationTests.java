package com.marvel.dyno;

import com.marvel.dyno.domain.DelayJobDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DynoQueuesDemoApplicationTests {

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;

	@Test
	public void stringTest(){
		ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
		valueOperations.set("hello", "redis");
		System.out.println("useRedisDao = " + valueOperations.get("hello"));
	}


	@Test
	public void testDO() {
		DelayJobDO delayJobDO = new DelayJobDO();
		delayJobDO.setId(1L);
		delayJobDO.setReferenceTime(System.currentTimeMillis());
		delayJobDO.setDelay(10);
		delayJobDO.setTopic("marvel");
		delayJobDO.setTtr(1);
		delayJobDO.setBody("{\"message\":\"some message\"}");
		System.out.println(delayJobDO.toString());
	}

	@Test
	public void contextLoads() {
	}

}
