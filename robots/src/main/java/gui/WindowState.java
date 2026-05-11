package gui;

import java.io.Serializable;

public class WindowState implements Serializable {
    private static final long serialVersionUID = 1L;

    // id — программный идентификатор окна, например "log" или "game"
    private String id;
    private int x, y, width, height;
    private boolean visible;
    private boolean icon; // свернуто (iconified)
    private boolean maximized;
    public WindowState() {};

    public WindowState(String id, int x, int y, int width, int height,
                       boolean visible, boolean icon, boolean maximized) {
        this.id = id;
        this.x = x; this.y = y; this.width = width; this.height = height;
        this.visible = visible; this.icon = icon; this.maximized = maximized;
    }

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean isIcon() { return icon; }
    public boolean isMaximized() { return maximized; }
    public void setId(String id) { this.id = id; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setIcon(boolean icon) { this.icon = icon; }
    public void setMaximized(boolean maximized) { this.maximized = maximized; }
}