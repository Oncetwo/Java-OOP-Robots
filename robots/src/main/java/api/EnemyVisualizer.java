package api;
import java.awt.Graphics2D;

public interface EnemyVisualizer {
    void draw(Graphics2D g, EnemyBehavior behavior);
}