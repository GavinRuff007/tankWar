package org.example.entities;

import org.example.game.SoundPlayer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class Tank {

    // ─── Health & Invincibility ────────────────────────────
    private int health = 3;
    private boolean invincible = false;
    private long invincibleStartTime = 0;
    private static final long INVINCIBLE_DURATION = 4000; // 4 ثانیه

    // ─── Position & Movement ───────────────────────────────
    private double x, y;
    private double angle;
    private double speed = 3.0;
    private double rotateSpeed = 3.0;

    // ─── Turret ────────────────────────────────────────────
    private double turretAngle;

    // ─── Shooting ──────────────────────────────────────────
    private List<Bullet> bullets = new ArrayList<>();
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 400;
    private long muzzleFlashTime = 0;
    private static final long FLASH_DURATION = 80;

    // ─── Appearance ────────────────────────────────────────
    private Color color;
    private Color bodyColor;
    private Color turretColor;
    private Color trackColor;
    private Color bulletColor;

    private static final int BODY_W = 52;
    private static final int BODY_H = 38;
    private static final int TURRET_R = 13;
    private static final int BARREL_LEN = 28;
    private static final int BARREL_W = 6;
    private static final int TRACK_W = 8;

    // ─── State ─────────────────────────────────────────────
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean rotatingLeft = false;
    private boolean rotatingRight = false;

    // ─── Controls ──────────────────────────────────────────
    private String upKey, downKey, leftKey, rightKey, shootKey;

    // ─── Constructor ───────────────────────────────────────
    public Tank(double x, double y, Color color, String upKey, String downKey,
                String leftKey, String rightKey, String shootKey) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.bodyColor = color;
        this.turretColor = color.darker();
        this.trackColor = color.darker();
        this.bulletColor = new Color(255, 200, 50);
        this.upKey = upKey;
        this.downKey = downKey;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.shootKey = shootKey;
    }

    // ─── Health Methods ────────────────────────────────────
    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage() {
        if (invincible) return;

        health--;
        SoundPlayer.playSound("sounds/explosion.wav");

        if (isAlive()) {
            invincible = true;
            invincibleStartTime = System.currentTimeMillis();
        }
    }

    public boolean isInvincible() {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            if (elapsed >= INVINCIBLE_DURATION) {
                invincible = false;
            }
        }
        return invincible;
    }

    public int getHealth() {
        return health;
    }

    // ─── Update ────────────────────────────────────────────
    public void update(int panelW, int panelH) {
        if (rotatingLeft) angle -= rotateSpeed;
        if (rotatingRight) angle += rotateSpeed;

        double rad = Math.toRadians(angle);

        if (movingForward) {
            x += Math.cos(rad) * speed;
            y += Math.sin(rad) * speed;
        }
        if (movingBackward) {
            x -= Math.cos(rad) * speed;
            y -= Math.sin(rad) * speed;
        }

        int margin = 20 + BODY_W / 2;
        x = Math.max(margin, Math.min(panelW - margin, x));
        y = Math.max(margin, Math.min(panelH - margin, y));

        bullets.removeIf(b -> !b.isActive());
        for (Bullet b : bullets) b.update(panelW, panelH);
    }

    // ─── Shoot ─────────────────────────────────────────────
    public void shoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTime < SHOOT_COOLDOWN) return;

        double[] tip = getBarrelTip();
        double totalAngle = angle + turretAngle;
        bullets.add(new Bullet(tip[0], tip[1], totalAngle, bulletColor));
        lastShotTime = now;
        muzzleFlashTime = now;
        SoundPlayer.playSound("sounds/shoot.wav");
    }

    // ─── Draw ──────────────────────────────────────────────
    public void draw(Graphics2D g2) {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibleStartTime;
            if ((elapsed / 200) % 2 == 0) {
                return;
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }

        for (Bullet b : bullets) b.draw(g2);

        AffineTransform saved = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(angle));

        drawTracks(g2);
        drawBody(g2);
        drawTurret(g2);
        draw3DEffect(g2);

        g2.setTransform(saved);

        if (invincible) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        drawHealthBar(g2);
    }

    private void drawTracks(Graphics2D g2) {
        int hw = BODY_W / 2;
        int hh = BODY_H / 2;

        g2.setColor(trackColor.darker());
        g2.fillRect(-hw - TRACK_W + 2, -hh + 2, TRACK_W, BODY_H - 2);
        g2.fillRect(hw - 2, -hh + 2, TRACK_W, BODY_H - 2);

        g2.setColor(trackColor);
        g2.fillRoundRect(-hw - TRACK_W, -hh, TRACK_W, BODY_H, 4, 4);
        g2.fillRoundRect(hw, -hh, TRACK_W, BODY_H, 4, 4);

        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(1.5f));
        int segments = 7;
        int segH = BODY_H / segments;
        for (int i = 1; i < segments; i++) {
            int ty = -hh + i * segH;
            g2.drawLine(-hw - TRACK_W, ty, -hw, ty);
            g2.drawLine(hw, ty, hw + TRACK_W, ty);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawBody(Graphics2D g2) {
        int hw = BODY_W / 2;
        int hh = BODY_H / 2;

        g2.setColor(bodyColor.darker().darker());
        g2.fillRoundRect(-hw + 3, -hh + 3, BODY_W, BODY_H, 6, 6);

        g2.setColor(bodyColor);
        g2.fillRoundRect(-hw, -hh, BODY_W, BODY_H, 6, 6);

        g2.setColor(bodyColor.brighter());
        g2.fillRoundRect(-hw + 4, -hh + 4, BODY_W - 16, BODY_H - 8, 4, 4);

        g2.setColor(bodyColor.darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(-hw, -hh, BODY_W, BODY_H, 6, 6);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawTurret(Graphics2D g2) {
        AffineTransform t = g2.getTransform();
        g2.rotate(Math.toRadians(turretAngle));

        g2.setColor(turretColor.darker());
        g2.fillOval(-TURRET_R + 2, -TURRET_R + 2, TURRET_R * 2, TURRET_R * 2);

        g2.setColor(turretColor.darker());
        g2.fillRoundRect(2, -BARREL_W / 2 + 2, BARREL_LEN, BARREL_W, 3, 3);

        g2.setColor(turretColor);
        g2.fillRoundRect(0, -BARREL_W / 2, BARREL_LEN, BARREL_W, 3, 3);

        g2.setColor(turretColor.darker().darker());
        g2.fillOval(BARREL_LEN - 4, -BARREL_W / 2 - 1, BARREL_W + 2, BARREL_W + 2);
        g2.setColor(new Color(20, 20, 20));
        g2.fillOval(BARREL_LEN - 2, -BARREL_W / 2 + 1, BARREL_W - 2, BARREL_W - 2);

        long now = System.currentTimeMillis();
        if (now - muzzleFlashTime < FLASH_DURATION) {
            float prog = 1f - (float) (now - muzzleFlashTime) / FLASH_DURATION;
            int flashSize = (int) (20 * prog);

            g2.setColor(new Color(1f, 0.9f, 0.3f, 0.9f * prog));
            g2.fillOval(BARREL_LEN - flashSize / 2, -flashSize / 2, flashSize, flashSize);
            g2.setColor(new Color(1f, 1f, 1f, 0.7f * prog));
            g2.fillOval(BARREL_LEN - flashSize / 4, -flashSize / 4, flashSize / 2, flashSize / 2);
        }

        g2.setColor(turretColor);
        g2.fillOval(-TURRET_R, -TURRET_R, TURRET_R * 2, TURRET_R * 2);

        g2.setColor(turretColor.brighter());
        g2.fillOval(-TURRET_R + 3, -TURRET_R + 3, TURRET_R - 2, TURRET_R - 2);

        g2.setColor(turretColor.darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(-TURRET_R, -TURRET_R, TURRET_R * 2, TURRET_R * 2);
        g2.setStroke(new BasicStroke(1f));

        g2.setTransform(t);
    }

    private void draw3DEffect(Graphics2D g2) {
        int hw = BODY_W / 2;
        int hh = BODY_H / 2;
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillRoundRect(-hw, -hh, BODY_W / 2, BODY_H / 2, 6, 6);
    }

    private void drawHealthBar(Graphics2D g2) {
        int heartSize = 16;
        int spacing = 5;
        int startX = (int) x - (3 * heartSize + 2 * spacing) / 2;
        int startY = (int) y + 35;

        for (int i = 0; i < 3; i++) {
            int hx = startX + i * (heartSize + spacing);

            if (i < health) {
                g2.setColor(new Color(220, 20, 60));
                g2.fillOval(hx, startY, heartSize / 2, heartSize / 2);
                g2.fillOval(hx + heartSize / 2, startY, heartSize / 2, heartSize / 2);

                int[] xPoints = {hx, hx + heartSize, hx + heartSize / 2};
                int[] yPoints = {startY + heartSize / 3, startY + heartSize / 3, startY + heartSize};
                g2.fillPolygon(xPoints, yPoints, 3);

                g2.setColor(new Color(255, 100, 100));
                g2.fillOval(hx + 3, startY + 2, 4, 4);
            } else {
                g2.setColor(new Color(200, 200, 200, 100));
                g2.fillOval(hx, startY, heartSize / 2, heartSize / 2);
                g2.fillOval(hx + heartSize / 2, startY, heartSize / 2, heartSize / 2);

                int[] xPoints = {hx, hx + heartSize, hx + heartSize / 2};
                int[] yPoints = {startY + heartSize / 3, startY + heartSize / 3, startY + heartSize};
                g2.fillPolygon(xPoints, yPoints, 3);

                g2.setColor(new Color(150, 150, 150, 150));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(hx, startY, heartSize / 2, heartSize / 2);
                g2.drawOval(hx + heartSize / 2, startY, heartSize / 2, heartSize / 2);
                g2.drawPolygon(xPoints, yPoints, 3);
                g2.setStroke(new BasicStroke(1f));
            }
        }
    }

    // ─── Turret Aim ────────────────────────────────────────
    public void aimTurretAt(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double worldAngle = Math.toDegrees(Math.atan2(dy, dx));
        turretAngle = worldAngle - angle;
    }

    // ─── Input Setters ─────────────────────────────────────
    public void setMovingForward(boolean v) { movingForward = v; }
    public void setMovingBackward(boolean v) { movingBackward = v; }
    public void setRotatingLeft(boolean v) { rotatingLeft = v; }
    public void setRotatingRight(boolean v) { rotatingRight = v; }

    // ─── Getters ───────────────────────────────────────────
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public double getTurretAngle() { return turretAngle; }
    public List<Bullet> getBullets() { return bullets; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) (x - 25), (int) (y - 25), 50, 50);
    }

    public double[] getBarrelTip() {
        double totalAngle = Math.toRadians(angle + turretAngle);
        double tipX = x + Math.cos(totalAngle) * (TURRET_R + BARREL_LEN);
        double tipY = y + Math.sin(totalAngle) * (TURRET_R + BARREL_LEN);
        return new double[]{tipX, tipY};
    }

    public Color getColor() {return color;}
}
