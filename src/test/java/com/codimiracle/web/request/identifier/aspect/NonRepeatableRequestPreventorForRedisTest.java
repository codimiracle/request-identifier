package com.codimiracle.web.request.identifier.aspect;

import com.codimiracle.web.request.identifier.Controller;
import com.codimiracle.web.request.identifier.TestApplicationWithoutResultHandler;
import com.codimiracle.web.request.identifier.exception.RepeatSubmissionException;
import com.codimiracle.web.request.identifier.provider.NonRepeatableRedisProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = TestApplicationWithoutResultHandler.class)
class NonRepeatableRequestPreventorForRedisTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    private Controller controller;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

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

    @Test
    void testRequestParameter() throws Exception {
        redisTemplate.opsForHyperLogLog().delete(NonRepeatableRedisProviderImpl.NON_REPEATABLE_KEY_ONCE);
        mvc.perform(get("/hello").queryParam("request_id", "abcd"))
                .andExpect(content().string("Accepted"));
    }
}