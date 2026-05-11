package api.maps;

import api.GameMap;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArenaMap implements GameMap {
    private final List<Shape> obstacles = new ArrayList<>();

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
    }

    @Override
    public String getName() {
        return "map.arena";
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        
        for (Shape wall : obstacles) {
            g.fill(wall); // Заливка
            
        }

    }
}