package api;

public interface EnemyBehavior {
    // Врагу нужен контекст карты и цель (робот) для преследования
    void update(GameContext context, RobotBehavior target);

    // Проверка, поймал ли враг робота
    boolean isCatching(RobotBehavior target);

    // Сброс врага на стартовую позицию при перезапуске
    void reset();

    double getX();
    double getY();
}