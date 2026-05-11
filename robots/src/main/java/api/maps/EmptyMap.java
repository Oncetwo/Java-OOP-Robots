package api.maps;

import api.GameMap;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

public class EmptyMap implements GameMap {
    private final List<Shape> obstacles = new ArrayList<>(); // Список пустой, так как препятствий нет 

    @Override
    public String getName() {
        return "map.empty";
    }

    @Override
    public List<Shape> getObstacles() {
        return obstacles; // Возвращаем пустой список 
    }

    @Override
    public void draw(Graphics2D g) {
    }
}