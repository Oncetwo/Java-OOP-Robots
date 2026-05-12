package api.maps;

import api.GameMap;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class CrossMap implements GameMap {
    private final List<Shape> obstacles = new ArrayList<>();

    public CrossMap() {
        // 1. Внешние границы (Рамка 800x800)
        obstacles.add(new Rectangle(0, 0, 800, 20));    // Верх
        obstacles.add(new Rectangle(0, 780, 800, 20));  // Низ
        obstacles.add(new Rectangle(0, 0, 20, 800));    // Лево
        obstacles.add(new Rectangle(780, 0, 20, 800));  // Право

      
        obstacles.add(new Rectangle(20, 150, 630, 20));
       
        obstacles.add(new Rectangle(150, 300, 630, 20));
        
        obstacles.add(new Rectangle(20, 450, 630, 20));
        
        obstacles.add(new Rectangle(150, 600, 630, 20));
    }

    @Override
    public String getName() {
        return "map.cross"; 
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles;
    }

    // Финиш в свободном углу (справа вверху)
    private final Shape finishZone = new Rectangle2D.Double(730, 20, 50, 50);

    @Override
    public Shape getFinishZone() { return finishZone; }

    @Override
    public void draw(Graphics2D g) {
        // Отрисовка ваших препятствий (rect1, rect2)
        g.setColor(Color.BLACK);
        for (Shape s : getObstacles()) g.fill(s);

        // Отрисовка финиша
        g.setColor(new Color(255, 215, 0));
        g.fill(finishZone);
    }
}