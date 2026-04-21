package org.example.entities;

import java.awt.*;

public class SpawnBase {
    private int x, y;
    private int size = 200;
    private int wallThickness = 15;
    private String doorSide;

    public SpawnBase(int x, int y, String doorSide) {
        this.x = x;
        this.y = y;
        this.doorSide = doorSide;
    }



    public void draw(Graphics2D g2) {
        g2.setColor(new Color(70, 70, 80));

        int doorSize = 80;

        // دیوار بالا
        if (!doorSide.equals("top")) {
            g2.fillRect(x, y, size, wallThickness);
        } else {
            g2.fillRect(x, y, (size - doorSize) / 2, wallThickness);
            g2.fillRect(x + (size + doorSize) / 2, y, (size - doorSize) / 2, wallThickness);
        }

        // دیوار پایین
        if (!doorSide.equals("bottom")) {
            g2.fillRect(x, y + size - wallThickness, size, wallThickness);
        } else {
            g2.fillRect(x, y + size - wallThickness, (size - doorSize) / 2, wallThickness);
            g2.fillRect(x + (size + doorSize) / 2, y + size - wallThickness, (size - doorSize) / 2, wallThickness);
        }

        // دیوار چپ
        if (!doorSide.equals("left")) {
            g2.fillRect(x, y, wallThickness, size);
        } else {
            g2.fillRect(x, y, wallThickness, (size - doorSize) / 2);
            g2.fillRect(x, y + (size + doorSize) / 2, wallThickness, (size - doorSize) / 2);
        }

        // دیوار راست
        if (!doorSide.equals("right")) {
            g2.fillRect(x + size - wallThickness, y, wallThickness, size);
        } else {
            g2.fillRect(x + size - wallThickness, y, wallThickness, (size - doorSize) / 2);
            g2.fillRect(x + size - wallThickness, y + (size + doorSize) / 2, wallThickness, (size - doorSize) / 2);
        }
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawRect(x - 2, y - 2, size + 4, size + 4);
    }

    public boolean intersectsWall(Rectangle rect) {
        int doorSize = 80;

        // دیوار بالا
        if (!doorSide.equals("top")) {
            if (rect.intersects(new Rectangle(x, y, size, wallThickness))) return true;
        } else {
            if (rect.intersects(new Rectangle(x, y, (size - doorSize) / 2, wallThickness))) return true;
            if (rect.intersects(new Rectangle(x + (size + doorSize) / 2, y, (size - doorSize) / 2, wallThickness))) return true;
        }

        // دیوار پایین
        if (!doorSide.equals("bottom")) {
            if (rect.intersects(new Rectangle(x, y + size - wallThickness, size, wallThickness))) return true;
        } else {
            if (rect.intersects(new Rectangle(x, y + size - wallThickness, (size - doorSize) / 2, wallThickness))) return true;
            if (rect.intersects(new Rectangle(x + (size + doorSize) / 2, y + size - wallThickness, (size - doorSize) / 2, wallThickness))) return true;
        }

        // دیوار چپ
        if (!doorSide.equals("left")) {
            if (rect.intersects(new Rectangle(x, y, wallThickness, size))) return true;
        } else {
            if (rect.intersects(new Rectangle(x, y, wallThickness, (size - doorSize) / 2))) return true;
            if (rect.intersects(new Rectangle(x, y + (size + doorSize) / 2, wallThickness, (size - doorSize) / 2))) return true;
        }

        // دیوار راست
        if (!doorSide.equals("right")) {
            if (rect.intersects(new Rectangle(x + size - wallThickness, y, wallThickness, size))) return true;
        } else {
            if (rect.intersects(new Rectangle(x + size - wallThickness, y, wallThickness, (size - doorSize) / 2))) return true;
            if (rect.intersects(new Rectangle(x + size - wallThickness, y + (size + doorSize) / 2, wallThickness, (size - doorSize) / 2))) return true;
        }

        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isInsideBase(int px, int py) {
        return px >= x && px <= x + size && py >= y && py <= y + size;
    }
}
