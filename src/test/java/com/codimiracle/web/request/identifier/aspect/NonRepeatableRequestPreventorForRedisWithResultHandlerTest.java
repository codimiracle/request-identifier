package com.codimiracle.web.request.identifier.aspect;

import com.codimiracle.web.request.identifier.Controller;
import com.codimiracle.web.request.identifier.TestApplication;
import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplication.class)
class NonRepeatableRequestPreventorForRedisWithResultHandlerTest {
    @Autowired
    private Controller controller;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    void testOnce() throws InterruptedException {
        redisTemplate.opsForHyperLogLog().delete(NonRepeatableRedisProviderImpl.NON_REPEATABLE_KEY_ONCE);
        assertEquals("Your submission is accepted.", controller.onlyOnce("Hello world"));
        assertEquals("repeated", controller.onlyOnce("Hello world"));
        Thread.sleep(3000);
        assertEquals("repeated", controller.onlyOnce("Hello world"));
    }

    @Test
    void testInterval() throws InterruptedException {
        assertEquals("OK", controller.interval("Hello", true));
        assertEquals("repeated", controller.interval("Hello", true));
        Thread.sleep(3000);
        assertEquals("OK", controller.interval("Hello", true));
    }

    @Test
    void testIntervalCustom() throws InterruptedException {
        assertEquals("Submitted", controller.intervalCustom("Hello", "world"));
        assertEquals("repeated", controller.intervalCustom("Hello", "world"));
        Thread.sleep(3000);
        assertEquals("Submitted", controller.intervalCustom("Hello", "world"));
    }
}