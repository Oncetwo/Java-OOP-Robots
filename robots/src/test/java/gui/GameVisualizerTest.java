package gui;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GameVisualizerTest {

    private Class<?> gvClass;

    @BeforeEach
    public void init() throws Exception {
        gvClass = Class.forName("gui.GameVisualizer");
    }

    @Test
    public void testApplyLimits() throws Exception {
        Method applyLimits = gvClass.getDeclaredMethod("applyLimits", double.class, double.class, double.class);
        applyLimits.setAccessible(true);
        double r1 = (double) applyLimits.invoke(null, -1.0, 0.0, 1.0);
        assertEquals(0.0, r1, 1e-9);
        double r2 = (double) applyLimits.invoke(null, 2.0, 0.0, 1.0);
        assertEquals(1.0, r2, 1e-9);
        double r3 = (double) applyLimits.invoke(null, 0.5, 0.0, 1.0);
        assertEquals(0.5, r3, 1e-9);
    }

    @Test
    public void testAsNormalizedRadians() throws Exception {
        Method asNorm = gvClass.getDeclaredMethod("asNormalizedRadians", double.class);
        asNorm.setAccessible(true);
        double val = (double) asNorm.invoke(null, -Math.PI/2.0);
        assertTrue(val >= 0 && val < 2*Math.PI);
        double val2 = (double) asNorm.invoke(null, 2*Math.PI + 0.1);
        assertEquals(0.1, val2, 1e-9);
    }

    @Test
    public void testMoveRobotStraightLine() throws Exception {
        Object gv = gvClass.getDeclaredConstructor().newInstance();

        Field px = gvClass.getDeclaredField("m_robotPositionX");
        Field py = gvClass.getDeclaredField("m_robotPositionY");
        Field dir = gvClass.getDeclaredField("m_robotDirection");
        px.setAccessible(true); py.setAccessible(true); dir.setAccessible(true);
        px.setDouble(gv, 0.0);
        py.setDouble(gv, 0.0);
        dir.setDouble(gv, 0.0);

        // call private moveRobot
        Method moveRobot = gvClass.getDeclaredMethod("moveRobot", double.class, double.class, double.class);
        moveRobot.setAccessible(true);

        moveRobot.invoke(gv, 0.05, 0.0, 10.0);

        double newX = px.getDouble(gv);
        double newY = py.getDouble(gv);
        double newDir = dir.getDouble(gv);

        assertEquals(0.5, newX, 1e-6);
        assertEquals(0.0, newY, 1e-6);
        assertTrue(newDir >= 0.0 && newDir < 2*Math.PI);
    }

    @Test
    public void testMoveRobotWithAngularVelocity() throws Exception {
        Object gv = gvClass.getDeclaredConstructor().newInstance();

        Field px = gvClass.getDeclaredField("m_robotPositionX");
        Field py = gvClass.getDeclaredField("m_robotPositionY");
        Field dir = gvClass.getDeclaredField("m_robotDirection");
        px.setAccessible(true); py.setAccessible(true); dir.setAccessible(true);
        px.setDouble(gv, 100.0);
        py.setDouble(gv, 100.0);
        dir.setDouble(gv, 0.0);

        Method moveRobot = gvClass.getDeclaredMethod("moveRobot", double.class, double.class, double.class);
        moveRobot.setAccessible(true);

        // nonzero angular velocity
        moveRobot.invoke(gv, 0.08, 0.0005, 10.0);

        double nx = px.getDouble(gv);
        double ny = py.getDouble(gv);
        double nd = dir.getDouble(gv);

        assertFalse(Double.isNaN(nx));
        assertFalse(Double.isNaN(ny));
        assertTrue(nd >= 0.0 && nd < 2*Math.PI);
    }
}