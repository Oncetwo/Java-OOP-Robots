package api.maps;

import api.*;
import api.enemies.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ArenaMap implements GameMap {
    private final List<Shape> obstacles = new ArrayList<>();
    private final List<IEnemy> enemies = new ArrayList<>();

    public ArenaMap() {
        // Внешняя рамка мира 800x800
        obstacles.add(new Rectangle(0, 0, 800, 20));    // Верх
        obstacles.add(new Rectangle(0, 780, 800, 20));  // Низ
        obstacles.add(new Rectangle(0, 0, 20, 800));    // Лево
        obstacles.add(new Rectangle(780, 0, 20, 800));  // Право

        // Стены спирали
        obstacles.add(new Rectangle(150, 0, 20, 650));
        
        obstacles.add(new Rectangle(150, 650, 500, 20));
       
        obstacles.add(new Rectangle(650, 150, 20, 520));
        
        obstacles.add(new Rectangle(300, 150, 370, 20));

        obstacles.add(new Rectangle(300, 150, 20, 350));
        
        obstacles.add(new Rectangle(300, 500, 200, 20));
        
        obstacles.add(new Rectangle(500, 320, 20, 200));

        enemies.add(new AlienEnemy(600, 100));
        enemies.add(new AlienEnemy(100, 600));


    }

    @Override
    public String getName() {
        return "map.arena";
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles;
    }

    private final Shape finishZone = new Rectangle2D.Double(375, 375, 50, 50);

    @Override
    public Shape getFinishZone() { return finishZone; }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        for (Shape s : getObstacles()) g.fill(s);

        // Золотой квадрат в центре
        g.setColor(new Color(255, 215, 0));
        g.fill(finishZone);
    }

    @Override
    public List<api.IEnemy> getEnemies() {
        return enemies;
    }
}