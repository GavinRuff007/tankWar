package org.example.entities;


import java.awt.*;
import java.awt.geom.AffineTransform;

public class Bullet {

    private double x, y;
    private double vx, vy;
    private boolean active = true;

    private static final double SPEED   = 10.0;
    private static final int    RADIUS  = 5;
    private static final int    TRAIL   = 12;   // طول دنباله


    // تاریخچه موقعیت برای دنباله
    private double[] trailX = new double[TRAIL];
    private double[] trailY = new double[TRAIL];
    private int trailIdx = 0;

    private Color color;
    private long  birthTime;

    private boolean explosionSpawned = false;  // ← اضافه شد

    // ... بقیه کدها ثابت می‌مانند ...

    // در انتهای کلاس این دو متد را اضافه کنید:
    public boolean isExplosionSpawned() { return explosionSpawned; }
    public void markExplosionSpawned()  { explosionSpawned = true; }

    public Bullet(double x, double y, double angleDeg, Color color) {
        this.x = x;
        this.y = y;
        double rad = Math.toRadians(angleDeg);
        this.vx = Math.cos(rad) * SPEED;
        this.vy = Math.sin(rad) * SPEED;
        this.color = color;
        this.birthTime = System.currentTimeMillis();

        // پر کردن دنباله با موقعیت شروع
        for (int i = 0; i < TRAIL; i++) {
            trailX[i] = x;
            trailY[i] = y;
        }
    }

    public void update(int panelW, int panelH) {
        if (!active) return;

        // ذخیره موقعیت قبلی در دنباله
        trailX[trailIdx] = x;
        trailY[trailIdx] = y;
        trailIdx = (trailIdx + 1) % TRAIL;

        x += vx;
        y += vy;

        // اگر از صفحه خارج شد غیرفعال شود
        if (x < 0 || x > panelW || y < 0 || y > panelH) {
            active = false;
        }

    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        // ─── رسم دنباله ───────────────────────────────────
        for (int i = 0; i < TRAIL - 1; i++) {
            int idx = (trailIdx + i) % TRAIL;
            float alpha = (float) i / TRAIL * 0.5f;
            int size = Math.max(1, (int)(RADIUS * ((float) i / TRAIL)));
            g2.setColor(new Color(
                    color.getRed()  / 255f,
                    color.getGreen()/ 255f,
                    color.getBlue() / 255f,
                    alpha
            ));
            g2.fillOval(
                    (int)(trailX[idx] - size / 2.0),
                    (int)(trailY[idx] - size / 2.0),
                    size, size
            );
        }

        // ─── گلوله اصلی ───────────────────────────────────
        // سایه
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillOval((int)(x - RADIUS + 2), (int)(y - RADIUS + 2), RADIUS * 2, RADIUS * 2);

        // بدنه گلوله
        g2.setColor(color);
        g2.fillOval((int)(x - RADIUS), (int)(y - RADIUS), RADIUS * 2, RADIUS * 2);

        // بازتاب نور
        g2.setColor(new Color(255, 255, 200, 180));
        g2.fillOval((int)(x - RADIUS / 2), (int)(y - RADIUS), RADIUS, RADIUS);
    }

    // ─── Getters ──────────────────────────────────────────
    public double getX()     { return x; }
    public double getY()     { return y; }
    public boolean isActive(){ return active; }
    public void deactivate() { active = false; }

    public Rectangle getBounds() {
        return new Rectangle((int)(x - RADIUS), (int)(y - RADIUS), RADIUS * 2, RADIUS * 2);
    }

    public void setActive(boolean active){this.active = active;}


}
