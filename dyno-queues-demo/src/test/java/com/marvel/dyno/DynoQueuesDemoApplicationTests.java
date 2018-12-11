package com.marvel.dyno;

import com.marvel.dyno.domain.DelayMessageDO;
import com.marvel.dyno.redis.RedisService;
import com.marvel.dyno.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DynoQueuesDemoApplicationTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedisService redisService;

    @Test
    public void stringTest() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("hello", "redis");
        System.out.println("useRedisDao = " + valueOperations.get("hello"));
    }


    @Test
    public void contextLoads() {
    }

    @Test
    public void batchAddJob() {


        for (long i = 0; i < 30L; i++) {
            /*DelayMessageDO delayMessageDO = new DelayMessageDO();
            delayMessageDO.setId(i);
            delayMessageDO.setReferenceTime(System.currentTimeMillis() / 1000);
            final double d = Math.random();
            final int time = (int) (d * 100);
            delayMessageDO.setDelay(time);
            delayMessageDO.setTopic("order-business");
            delayMessageDO.setTtr(1L);
            delayMessageDO.setBody("{\"message\":\"some message\"}");
            System.out.println(delayMessageDO.toString());

            redisService.zadd(
                    delayMessageDO.getTopic(),
                    DateUtils.calculationDelayTime(delayMessageDO.getReferenceTime(), delayMessageDO.getDelay(), TimeUnit.SECONDS),
                    delayMessageDO.getId());

            redisService.hset(delayMessageDO.getTopic() + ":hash" , "" + delayMessageDO.getId(), delayMessageDO.toString(), -1);

*/
        }

    }

    @Test
    public void test() {
        long a = 1544414842L;
        System.out.println((double) (a + 10));
    }
}
