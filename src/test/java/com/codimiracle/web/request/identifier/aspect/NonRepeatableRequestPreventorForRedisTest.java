package com.codimiracle.web.request.identifier.aspect;

import com.codimiracle.web.request.identifier.Controller;
import com.codimiracle.web.request.identifier.TestApplicationWithoutResultHandler;
import com.codimiracle.web.request.identifier.exception.RepeatSubmissionException;
import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Repeat;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplicationWithoutResultHandler.class)
class NonRepeatableRequestPreventorForRedisTest {
    @Autowired
    private Controller controller;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    void testOnce() throws InterruptedException {
        redisTemplate.opsForHyperLogLog().delete(NonRepeatableRedisProviderImpl.NON_REPEATABLE_KEY_ONCE);
        controller.onlyOnce("Hello world");
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.onlyOnce("Hello world");
        });
        Thread.sleep(3000);
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.onlyOnce("Hello world");
        });
    }

    @Test
    void testInterval() throws InterruptedException {
        controller.interval("Hello", true);
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.interval("Hello", true);
        });
        Thread.sleep(3000);
        assertDoesNotThrow(() -> controller.interval("Hello", true));
    }

    @Test
    void testIntervalCustom() throws InterruptedException {
        controller.intervalCustom("Hello", "Hello world");
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.intervalCustom("Hello", "Hello world");
        });
        Thread.sleep(3000);
        assertDoesNotThrow(() -> controller.intervalCustom("Hello", "Hello world"));
    }

    @Test
    void testCustomArg() throws InterruptedException {
        redisTemplate.opsForHyperLogLog().delete(NonRepeatableRedisProviderImpl.NON_REPEATABLE_KEY_ONCE);
        Controller.CustomArg arg = new Controller.CustomArg();
        arg.setAge(10);
        arg.setName("Hello");
        arg.setStatus("created");
        assertEquals("ok", controller.customArg(arg));
        Controller.CustomArg anotherSame = new Controller.CustomArg();
        BeanUtils.copyProperties(arg, anotherSame);
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.customArg(anotherSame);
        });
        Thread.sleep(2000);
        assertThrows(RepeatSubmissionException.class, () -> {
            controller.customArg(anotherSame);
        });
    }
}