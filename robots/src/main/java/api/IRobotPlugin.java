package api;

public interface IRobotPlugin {
    String getName();
    RobotBehavior getBehavior();
    RobotVisualizer getVisualizer();
}