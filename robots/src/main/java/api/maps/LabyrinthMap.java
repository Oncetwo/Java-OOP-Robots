package api.maps;

import api.GameMap;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LabyrinthMap implements GameMap {
    private final List<Shape> obstacles = new ArrayList<>();
    
 // (x, y, ширина, высота)
    public LabyrinthMap() {
        // Внешняя рамка мира 800x800
        obstacles.add(new Rectangle(0, 0, 800, 20));    // Верх
        obstacles.add(new Rectangle(0, 780, 800, 20));  // Низ
        obstacles.add(new Rectangle(0, 0, 20, 800));    // Лево
        obstacles.add(new Rectangle(780, 0, 20, 800));  // Право

        
        obstacles.add(new Rectangle(130, 150, 220, 20));
        obstacles.add(new Rectangle(260, 150, 20, 400));
        obstacles.add(new Rectangle(130, 300, 20, 250));
       
        
        obstacles.add(new Rectangle(20, 500, 110, 20));
        obstacles.add(new Rectangle(130, 650, 320, 20));
        obstacles.add(new Rectangle(450, 450, 20, 330));
        
        
        obstacles.add(new Rectangle(350, 250, 250, 20));
        obstacles.add(new Rectangle(500, 250, 20, 200));
        obstacles.add(new Rectangle(350, 450, 270, 20));
        
        
        obstacles.add(new Rectangle(550, 650, 230, 20));
        obstacles.add(new Rectangle(550, 20, 20, 130));
        obstacles.add(new Rectangle(550, 150, 150, 20));
        obstacles.add(new Rectangle(700, 150, 20, 300));
    }

    @Override
    public String getName() {
        return "map.labyrinth";
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.RED); // Устанавливаем красный цвет для стен
        
        for (Shape wall : obstacles) {
            g.fill(wall); // Заливка стены
            
        }
    }
}