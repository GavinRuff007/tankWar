package org.example.entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class TankWreck {

    private double x, y, angle;
    private Color bodyColor;
    private long createTime;

    private List<SmokeParticle> smoke = new ArrayList<>();
    private List<Debris> debris = new ArrayList<>();

    private int flameTimer = 0;

    public TankWreck(double x, double y, double angle, Color bodyColor) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.bodyColor = bodyColor;
        this.createTime = System.currentTimeMillis();

        for (int i = 0; i < 20; i++) {
            smoke.add(new SmokeParticle(x, y - 15));
        }

        for (int i = 0; i < 12; i++) {
            debris.add(new Debris(x, y));
        }
    }

    public void draw(Graphics2D g2) {

        long elapsed = System.currentTimeMillis() - createTime;
        float alpha = Math.max(0.3f, 1f - elapsed / 10000f);

        // زمین سوخته
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval((int) (x - 40), (int) (y - 15), 80, 30);

        // دود
        smoke.forEach(p -> {
            p.update();
            p.draw(g2);
        });
        smoke.removeIf(SmokeParticle::dead);

        if (Math.random() < 0.25) {
            smoke.add(new SmokeParticle(x, y - 20));
        }

        // قطعات تانک
        debris.forEach(d -> {
            d.update();
            d.draw(g2);
        });

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        AffineTransform saved = g2.getTransform();

        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle));

        // بدنه سوخته
        Color burntColor = new Color(40, 40, 40);
        g2.setColor(burntColor);
        g2.fillRoundRect(-26, -19, 52, 38, 6, 6);

        // خطوط سوختگی
        g2.setColor(new Color(20, 20, 20));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(-20, -10, -10, 10);
        g2.drawLine(10, -10, 20, 10);

        // برجک کج شده
        AffineTransform turretSave = g2.getTransform();

        g2.rotate(Math.toRadians(40));

        g2.setColor(new Color(70, 70, 70));
        g2.fillOval(-12, -12, 24, 24);

        g2.setColor(new Color(50, 50, 50));
        g2.fillRect(0, -4, 18, 8);

        g2.setTransform(turretSave);

        g2.setTransform(saved);

        // شعله‌های کوچک
        flameTimer++;

        if (flameTimer % 5 == 0) {

            int fx = (int) (Math.random() * 20 - 10);
            int fy = (int) (Math.random() * 10 - 5);

            g2.setColor(new Color(255, 120, 0, 180));
            g2.fillOval((int) (x + fx - 4), (int) (y + fy - 20), 8, 8);

            g2.setColor(new Color(255, 200, 50, 160));
            g2.fillOval((int) (x + fx - 2), (int) (y + fy - 18), 4, 4);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }


    private static class SmokeParticle {

        double x, y;
        double vx, vy;
        float alpha = 1f;
        float size;

        SmokeParticle(double x, double y) {

            this.x = x + (Math.random() * 20 - 10);
            this.y = y + (Math.random() * 10 - 5);

            vx = (Math.random() * 1 - 0.5);
            vy = -0.5 - Math.random() * 1.5;

            size = 8 + (float) (Math.random() * 12);
        }

        void update() {
            x += vx;
            y += vy;

            vx *= 0.98;
            vy *= 0.98;

            alpha -= 0.008f;
            if (alpha < 0) alpha = 0;
        }

        void draw(Graphics2D g2) {

            g2.setColor(new Color(70, 70, 70, (int) (alpha * 90)));
            g2.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
        }

        boolean dead() {
            return alpha <= 0;
        }
    }


    private static class Debris {

        double x, y;
        double vx, vy;
        float rotation = 0;
        double rotSpeed;

        Debris(double x, double y) {

            this.x = x;
            this.y = y;

            vx = (Math.random() * 6 - 3);
            vy = (Math.random() * 6 - 3);

            rotSpeed = Math.random() * 10 - 5;
        }

        void update() {

            x += vx;
            y += vy;

            vx *= 0.94;
            vy *= 0.94;

            rotation += rotSpeed;
            rotSpeed *= 0.95;
        }

        void draw(Graphics2D g2) {

            AffineTransform saved = g2.getTransform();

            g2.translate(x, y);
            g2.rotate(Math.toRadians(rotation));

            g2.setColor(new Color(60, 60, 60));
            g2.fillRect(-4, -2, 8, 4);

            g2.setTransform(saved);
        }
    }
}
