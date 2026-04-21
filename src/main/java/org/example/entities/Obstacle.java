package org.example.entities;

import java.awt.*;

public class Obstacle {
    private int x, y, width, height;
    private Color color;

    public Obstacle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = new Color(101, 67, 33);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color.darker());
        g2.fillRect(x + 3, y + 3, width, height);
        
        g2.setColor(color);
        g2.fillRect(x, y, width, height);
        g2.setColor(color.brighter());
        g2.fillRect(x + 5, y + 5, width - 10, height - 10);
        g2.setColor(color.darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(x, y, width, height);
        g2.setStroke(new BasicStroke(1f));
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
