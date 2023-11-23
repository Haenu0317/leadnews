package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {
    @Resource
    CacheService cacheService;

    @Test
    public void testlist(){
        System.out.println(cacheService.lRightPop("list_001"));
    }

    @Test
    public void testZset(){
        Set<String> zsetKey001 = cacheService.zRange("zset_key_001", 0, 88888);
        System.out.println(zsetKey001);
    }
}
