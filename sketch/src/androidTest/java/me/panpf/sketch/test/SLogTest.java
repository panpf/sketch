package me.panpf.sketch.test;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static me.panpf.sketch.SLog.LEVEL_DEBUG;
import static me.panpf.sketch.SLog.LEVEL_ERROR;
import static me.panpf.sketch.SLog.LEVEL_INFO;
import static me.panpf.sketch.SLog.LEVEL_NONE;
import static me.panpf.sketch.SLog.LEVEL_VERBOSE;
import static me.panpf.sketch.SLog.LEVEL_WARNING;
import static me.panpf.sketch.SLog.TYPE_CACHE;
import static me.panpf.sketch.SLog.TYPE_FLOW;
import static me.panpf.sketch.SLog.TYPE_ZOOM_BLOCK_DISPLAY;
import static me.panpf.sketch.SLog.TYPE_TIME;
import static me.panpf.sketch.SLog.TYPE_ZOOM;
import static me.panpf.sketch.SLog.closeType;
import static me.panpf.sketch.SLog.isLoggable;
import static me.panpf.sketch.SLog.openType;
import static me.panpf.sketch.SLog.setLevel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SLogTest {

    @Test
    public void testTypeLoggable() {
        openType(TYPE_FLOW);
        assertTrue("TYPE_FLOW invalid", isLoggable(TYPE_FLOW));
        assertFalse("TYPE_CACHE valid", isLoggable(TYPE_CACHE));

        openType(TYPE_TIME);
        assertTrue("TYPE_TIME invalid", isLoggable(TYPE_TIME));
        assertFalse("TYPE_ZOOM invalid", isLoggable(TYPE_ZOOM));

        closeType(TYPE_FLOW);
        assertFalse("TYPE_FLOW valid", isLoggable(TYPE_FLOW));
    }

    @Test
    public void testLevelLoggable() {
        setLevel(LEVEL_INFO);

        assertTrue("LEVEL_INFO invalid", isLoggable(LEVEL_INFO));
        assertTrue("LEVEL_WARNING invalid", isLoggable(LEVEL_WARNING));
        assertTrue("LEVEL_ERROR invalid", isLoggable(LEVEL_ERROR));
        assertTrue("LEVEL_NONE invalid", isLoggable(LEVEL_NONE));

        assertFalse("LEVEL_DEBUG valid", isLoggable(LEVEL_DEBUG));
        assertFalse("LEVEL_VERBOSE valid", isLoggable(LEVEL_VERBOSE));
    }

    @Test
    public void testAssembleLoggable() {
        //noinspection WrongConstant
        setLevel(LEVEL_VERBOSE | LEVEL_ERROR);
        //noinspection WrongConstant
        openType(TYPE_ZOOM_BLOCK_DISPLAY | TYPE_TIME);
        assertTrue("LEVEL_ERROR invalid", isLoggable(LEVEL_ERROR));
        assertTrue("TYPE_ZOOM_BLOCK_DISPLAY invalid", isLoggable(TYPE_ZOOM_BLOCK_DISPLAY));
        assertTrue("TYPE_TIME invalid", isLoggable(TYPE_TIME));

        //noinspection WrongConstant
        closeType(TYPE_ZOOM_BLOCK_DISPLAY);
        assertFalse("TYPE_ZOOM_BLOCK_DISPLAY valid", isLoggable(TYPE_ZOOM_BLOCK_DISPLAY));

        //noinspection WrongConstant
        setLevel(LEVEL_WARNING);
        openType(TYPE_ZOOM);
        //noinspection WrongConstant
        assertTrue("LEVEL_WARNING and TYPE_TIME invalid", isLoggable(LEVEL_WARNING | TYPE_TIME));
    }

    @Test
    public void testTemp() {
        openType(TYPE_ZOOM_BLOCK_DISPLAY);
        assertTrue("LEVEL_INFO invalid", isLoggable(LEVEL_INFO));
        assertTrue("TYPE_ZOOM_BLOCK_DISPLAY invalid", isLoggable(TYPE_ZOOM_BLOCK_DISPLAY));
    }
}
