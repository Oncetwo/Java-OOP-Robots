package api.maps;

import api.GameMap;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class EmptyMap implements GameMap {
    private final Shape finishZone = new Rectangle2D.Double(730, 730, 50, 50);

    @Override
    public Shape getFinishZone() { return finishZone; }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 215, 0, 180)); // Золотистый полупрозрачный
        g.fill(finishZone);
        g.setColor(Color.ORANGE);
        g.draw(finishZone);
    }
    private final List<Shape> obstacles = new ArrayList<>(); // Список пустой, так как препятствий нет 

    @Override
    public String getName() {
        return "map.empty";
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles; // Возвращаем пустой список 
    }
}