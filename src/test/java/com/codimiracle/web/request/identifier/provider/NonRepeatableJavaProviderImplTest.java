package com.codimiracle.web.request.identifier.provider;

import com.codimiracle.web.request.identifier.TestApplicationWithoutRedisProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApplicationWithoutRedisProvider.class)
class NonRepeatableJavaProviderImplTest {
    @Autowired
    private NonRepeatableJavaProviderImpl nonRepeatableJavaProvider;

    @Test
    void testIsRepeat() throws InterruptedException {
        assertFalse(
                nonRepeatableJavaProvider.isRepeat("a")
        );
        assertTrue(
                nonRepeatableJavaProvider.isRepeat("a")
        );
        Thread.sleep(1000);
        assertTrue(
                nonRepeatableJavaProvider.isRepeat("a")
        );
    }

    @Test
    void testIsRepeatInterval() throws InterruptedException {
        assertFalse(
                nonRepeatableJavaProvider.isRepeat("a", 1000)
        );
        assertTrue(
                nonRepeatableJavaProvider.isRepeat("a", 1000)
        );
        Thread.sleep(1000);
        assertFalse(
                nonRepeatableJavaProvider.isRepeat("a", 1000)
        );
    }
}