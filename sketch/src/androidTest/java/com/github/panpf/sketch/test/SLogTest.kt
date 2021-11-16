package com.github.panpf.sketch.test;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.github.panpf.sketch.SLog.DEBUG;
import static com.github.panpf.sketch.SLog.ERROR;
import static com.github.panpf.sketch.SLog.INFO;
import static com.github.panpf.sketch.SLog.NONE;
import static com.github.panpf.sketch.SLog.VERBOSE;
import static com.github.panpf.sketch.SLog.WARNING;
import static com.github.panpf.sketch.SLog.isLoggable;
import static com.github.panpf.sketch.SLog.setLevel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SLogTest {

    @Test
    public void testLevelLoggable() {
        setLevel(INFO);

        assertTrue("LEVEL_INFO invalid", isLoggable(INFO));
        assertTrue("LEVEL_WARNING invalid", isLoggable(WARNING));
        assertTrue("LEVEL_ERROR invalid", isLoggable(ERROR));
        assertTrue("LEVEL_NONE invalid", isLoggable(NONE));

        assertFalse("LEVEL_DEBUG valid", isLoggable(DEBUG));
        assertFalse("LEVEL_VERBOSE valid", isLoggable(VERBOSE));
    }
}
