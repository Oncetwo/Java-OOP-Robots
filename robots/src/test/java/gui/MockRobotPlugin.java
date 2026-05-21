package gui;

import api.*;
import java.awt.Color;

public class MockRobotPlugin implements IRobotPlugin {

    public static final String ROBOT_NAME = "Тестовый Робот";
    public static final Color ROBOT_COLOR = Color.GREEN;

    private final RobotBehavior behavior = new RobotBehavior() {
        private double x = 100, y = 100, dir = 0;
        @Override public void update(GameContext c) {}
        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getDirection() { return dir; }
        @Override public void setPosition(double x, double y) { this.x = x; this.y = y; }
    };

    private final RobotVisualizer visualizer = (g, r) -> {
        g.setColor(ROBOT_COLOR);
        g.fillRect((int)r.getX(), (int)r.getY(), 20, 20);
    };

    @Override public String getName() { return ROBOT_NAME; }
    @Override public RobotBehavior getBehavior() { return behavior; }
    @Override public RobotVisualizer getVisualizer() { return visualizer; }
}