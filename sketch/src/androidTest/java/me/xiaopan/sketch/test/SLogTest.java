package me.xiaopan.sketch.test;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static me.xiaopan.sketch.SLog.LEVEL_DEBUG;
import static me.xiaopan.sketch.SLog.LEVEL_ERROR;
import static me.xiaopan.sketch.SLog.LEVEL_INFO;
import static me.xiaopan.sketch.SLog.LEVEL_NONE;
import static me.xiaopan.sketch.SLog.LEVEL_VERBOSE;
import static me.xiaopan.sketch.SLog.LEVEL_WARNING;
import static me.xiaopan.sketch.SLog.TYPE_CACHE;
import static me.xiaopan.sketch.SLog.TYPE_FLOW;
import static me.xiaopan.sketch.SLog.TYPE_HUGE_IMAGE;
import static me.xiaopan.sketch.SLog.TYPE_TIME;
import static me.xiaopan.sketch.SLog.TYPE_ZOOM;
import static me.xiaopan.sketch.SLog.closeType;
import static me.xiaopan.sketch.SLog.isLoggable;
import static me.xiaopan.sketch.SLog.openType;
import static me.xiaopan.sketch.SLog.setLevel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SLogTest {

    @Test
    public void testTypeLoggable() {
        openType(TYPE_FLOW);
        assertTrue("type request invalid", isLoggable(TYPE_FLOW));
        assertFalse("type cache valid", isLoggable(TYPE_CACHE));

        openType(TYPE_TIME);
        assertTrue("type time invalid", isLoggable(TYPE_TIME));
        assertFalse("type zoom invalid", isLoggable(TYPE_ZOOM));

        closeType(TYPE_FLOW);
        assertFalse("type request valid", isLoggable(TYPE_FLOW));
    }

    @Test
    public void testLevelLoggable() {
        setLevel(LEVEL_INFO);

        assertTrue("level info invalid", isLoggable(LEVEL_INFO));
        assertTrue("level warning invalid", isLoggable(LEVEL_WARNING));
        assertTrue("level error invalid", isLoggable(LEVEL_ERROR));
        assertTrue("level none invalid", isLoggable(LEVEL_NONE));

        assertFalse("level debug valid", isLoggable(LEVEL_DEBUG));
        assertFalse("level verbose valid", isLoggable(LEVEL_VERBOSE));
    }

    @Test
    public void testAssembleLoggable() {
        //noinspection WrongConstant
        setLevel(LEVEL_VERBOSE | LEVEL_ERROR);
        //noinspection WrongConstant
        openType(TYPE_HUGE_IMAGE | TYPE_TIME);
        assertTrue("level error invalid", isLoggable(LEVEL_ERROR));
        assertTrue("time huge image invalid", isLoggable(TYPE_HUGE_IMAGE));
        assertTrue("time time invalid", isLoggable(TYPE_TIME));

        //noinspection WrongConstant
        closeType(TYPE_HUGE_IMAGE);
        assertFalse("time huge image valid", isLoggable(TYPE_HUGE_IMAGE));

        //noinspection WrongConstant
        setLevel(LEVEL_WARNING);
        openType(TYPE_ZOOM);
        //noinspection WrongConstant
        assertTrue("LEVEL_WARNING and TYPE_TIME invalid", isLoggable(LEVEL_WARNING | TYPE_TIME));
    }

    @Test
    public void testTemp() {
        openType(TYPE_HUGE_IMAGE);
        assertTrue("level verbose invalid", isLoggable(LEVEL_INFO));
        assertTrue("type huge image invalid", isLoggable(TYPE_HUGE_IMAGE));
    }
}
