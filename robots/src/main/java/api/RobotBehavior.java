package api;

public interface RobotBehavior {
    void update(GameContext context);
    double getX();
    double getY();
    double getDirection(); // Нужно для отрисовки поворота
}