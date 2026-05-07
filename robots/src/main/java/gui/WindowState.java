package gui;

import java.io.Serializable;

public class WindowState implements Serializable {
    private static final long serialVersionUID = 1L;

    // id — программный идентификатор окна, например "log" или "game"
    private final String id;
    private final int x, y, width, height;
    private final boolean visible;
    private final boolean icon; // свернуто (iconified)
    private final boolean maximized;

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
}