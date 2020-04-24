package com.codimiracle.web.request.identifier.provider;

import com.codimiracle.web.request.identifier.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApplication.class)
class NonRepeatableRedisProviderImplTest {

    @Autowired
    private NonRepeatableRedisProviderImpl nonRepeatableRedisProvider;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testIsRepeat() throws InterruptedException {
        redisTemplate.opsForHyperLogLog().delete(NonRepeatableRedisProviderImpl.NON_REPEATABLE_KEY_ONCE);

        assertFalse(
                nonRepeatableRedisProvider.isRepeat("a")
        );
        assertTrue(
                nonRepeatableRedisProvider.isRepeat("a")
        );
        Thread.sleep(1000);
        assertTrue(
                nonRepeatableRedisProvider.isRepeat("a")
        );
    }

    @Test
    void testIsRepeatInterval() throws InterruptedException {
        assertFalse(
                nonRepeatableRedisProvider.isRepeat("a", 1000)
        );
        assertTrue(
                nonRepeatableRedisProvider.isRepeat("a", 1000)
        );
        Thread.sleep(1000);
        assertFalse(
                nonRepeatableRedisProvider.isRepeat("a", 1000)
        );
    }
}