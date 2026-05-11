package api;

public interface RobotBehavior {
    void update(GameContext context); // контекст для робота, чтобы он мог изменить свое поведение
    double getX();
    double getY();
    double getDirection(); // Нужно для отрисовки поворота (угол в радианах)
    void setPosition(double x, double y); // метод через который визуализатор будет телепортировать робота в начало карты
}