package org.example.game;

import org.example.entities.Obstacle;
import org.example.entities.SpawnBase;
import org.example.entities.Tank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.example.entities.TankWreck;


public class GamePanel extends JPanel implements ActionListener {

    private List<SpawnBase> spawnBases = new ArrayList<>();
    private List<TankWreck> wrecks = new ArrayList<>();
    private int WIDTH, HEIGHT;
    private List<Tank> tanks = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Timer gameTimer;
    private List<Explosion> explosions = new ArrayList<>();
    private int playerCount;
    private int numPlayers;


    public GamePanel(int playerCount) {
        this.numPlayers = playerCount;
        this.playerCount = playerCount;Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screen.width;
        HEIGHT = screen.height;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 85, 34));
        setFocusable(true);

        initTanks();
        initObstacles();

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                handleKey(e, true);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
            }@Override public void keyReleased(KeyEvent e) { handleKey(e, false); }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                if (!tanks.isEmpty()) tanks.get(0).aimTurretAt(e.getX(), e.getY());
            }
            @Override public void mouseDragged(MouseEvent e) {
                if (!tanks.isEmpty()) tanks.get(0).aimTurretAt(e.getX(), e.getY());
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && !tanks.isEmpty()) {
                    tanks.get(0).shoot();
                }
            }
        });

        gameTimer = new Timer(1000 / 60, this);
        gameTimer.start();
    }

    private void initTanks() {
        tanks.clear();
        spawnBases.clear();

        int margin = 20;
        int clearZone = 50;
        int baseSize = 200;
        int safeMargin = margin + clearZone;

        if (numPlayers == 1) {
            tanks.add(new Tank(WIDTH / 2, HEIGHT / 2, Color.BLUE, "W", "S", "A", "D", "SPACE"));spawnBases.add(new SpawnBase(WIDTH / 2 - baseSize / 2, HEIGHT / 2 - baseSize / 2, "bottom"));
        } else if (numPlayers == 2) {
            tanks.add(new Tank(safeMargin + 40, safeMargin + 40, Color.BLUE, "W", "S", "A", "D", "SPACE"));
            tanks.add(new Tank(WIDTH - safeMargin - baseSize + 40, HEIGHT - safeMargin - baseSize + 40, Color.RED, "UP", "DOWN", "LEFT", "RIGHT", "ENTER"));

            spawnBases.add(new SpawnBase(safeMargin, safeMargin, "bottom"));
            spawnBases.add(new SpawnBase(WIDTH - safeMargin - baseSize, HEIGHT - safeMargin - baseSize, "top"));
        } else if (numPlayers == 3) {
            tanks.add(new Tank(safeMargin + 40, safeMargin + 40, Color.BLUE, "W", "S", "A", "D", "SPACE"));
            tanks.add(new Tank(WIDTH - safeMargin - baseSize + 40, safeMargin + 40, Color.RED, "UP", "DOWN", "LEFT", "RIGHT", "ENTER"));
            tanks.add(new Tank(WIDTH / 2, HEIGHT - safeMargin - baseSize + 40, Color.GREEN, "I", "K", "J", "L", "O"));

            spawnBases.add(new SpawnBase(safeMargin, safeMargin, "right"));
            spawnBases.add(new SpawnBase(WIDTH - safeMargin - baseSize, safeMargin, "left"));
            spawnBases.add(new SpawnBase(WIDTH / 2 - baseSize / 2, HEIGHT - safeMargin - baseSize, "top"));
        } else if (numPlayers == 4) {
            tanks.add(new Tank(safeMargin + 40, safeMargin + 40, Color.BLUE, "W", "S", "A", "D", "SPACE"));
            tanks.add(new Tank(WIDTH - safeMargin - baseSize + 40, safeMargin + 40, Color.RED, "UP", "DOWN", "LEFT", "RIGHT", "ENTER"));
            tanks.add(new Tank(safeMargin + 40, HEIGHT - safeMargin - baseSize + 40, Color.GREEN, "I", "K", "J", "L", "O"));
            tanks.add(new Tank(WIDTH - safeMargin - baseSize + 40, HEIGHT - safeMargin - baseSize + 40, Color.YELLOW, "T", "G", "F", "H", "Y"));

            spawnBases.add(new SpawnBase(safeMargin, safeMargin, "bottom"));
            spawnBases.add(new SpawnBase(WIDTH - safeMargin - baseSize, safeMargin, "bottom"));
            spawnBases.add(new SpawnBase(safeMargin, HEIGHT - safeMargin - baseSize, "top"));
            spawnBases.add(new SpawnBase(WIDTH - safeMargin - baseSize, HEIGHT - safeMargin - baseSize, "top"));
        }
    }



    private void initObstacles() {
        Random rand = new Random();
        int margin = 20;
        int clearZone = 50;
        int baseSize = 200;

        for (int i = 0; i < 15; i++) {
            int x, y, w, h;
            boolean valid;
            int attempts = 0;

            do {
                valid = true;
                w = 40 + rand.nextInt(60);
                h = 40 + rand.nextInt(60);
                x = margin + clearZone + rand.nextInt(WIDTH - 2 * margin - 2 * clearZone - w);
                y = margin + clearZone + rand.nextInt(HEIGHT - 2 * margin - 2 * clearZone - h);

                Rectangle newObs = new Rectangle(x, y, w, h);

                // چک فاصله از SpawnBase ها
                for (SpawnBase base : spawnBases) {
                    Rectangle baseBounds = base.getBounds();
                    Rectangle expandedBase = new Rectangle(
                            baseBounds.x - clearZone,
                            baseBounds.y - clearZone,
                            baseBounds.width + 2 * clearZone,
                            baseBounds.height + 2 * clearZone
                    );
                    if (newObs.intersects(expandedBase)) {
                        valid = false;
                        break;
                    }
                }

                // چک همپوشانی با موانع دیگر
                if (valid) {
                    for (Obstacle obs : obstacles) {
                        Rectangle obsBounds = obs.getBounds();
                        Rectangle expandedObs = new Rectangle(
                                obsBounds.x - clearZone,
                                obsBounds.y - clearZone,
                                obsBounds.width + 2 * clearZone,
                                obsBounds.height + 2 * clearZone
                        );
                        if (newObs.intersects(expandedObs)) {
                            valid = false;
                            break;
                        }
                    }
                }

                attempts++;
            } while (!valid && attempts < 100);

            if (valid) {
                obstacles.add(new Obstacle(x, y, w, h));
            }
        }
    }


    private void handleKey(KeyEvent e, boolean pressed) {
        if (tanks.isEmpty()) return;
        Tank tank = tanks.get(0);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:
                tank.setMovingForward(pressed); break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
                tank.setMovingBackward(pressed); break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
                tank.setRotatingLeft(pressed); break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
                tank.setRotatingRight(pressed); break;
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGround(g2);
        drawBorders(g2);
        spawnBases.forEach(base -> base.draw(g2));  // رسم پایگاه‌ها
        obstacles.forEach(o -> o.draw(g2));
        explosions.forEach(ex -> ex.draw(g2));
        wrecks.forEach(w -> w.draw(g2));
        tanks.stream().filter(Tank::isAlive).forEach(tank -> tank.draw(g2));
        drawHUD(g2);
    }


    private void drawGround(Graphics2D g2) {
        g2.setColor(new Color(30, 80, 30));
        int gs = 50;
        for (int x = 0; x < WIDTH; x += gs)
            for (int y = 0; y < HEIGHT; y += gs)
                if ((x / gs + y / gs) % 2 == 0)
                    g2.fillRect(x, y, gs, gs);
    }

    private void drawBorders(Graphics2D g2) {
        g2.setColor(new Color(139, 90, 43));
        g2.setStroke(new BasicStroke(12f));
        g2.drawRect(6, 6, WIDTH - 12, HEIGHT - 12);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(10, 10, 200, 60, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Monospaced", Font.BOLD, 13));
        g2.drawString("بازیکنان: " + playerCount, 20, 32);
        g2.drawString("ESC - خروج", 20, 52);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tanks.forEach(tank -> {
            if (!tank.isAlive()) return;

            double oldX = tank.getX();
            double oldY = tank.getY();

            tank.update(WIDTH, HEIGHT);

            // چک برخورد تانک با موانع
            Rectangle tankBounds = tank.getBounds();
            for (Obstacle obs : obstacles) {
                if (tankBounds.intersects(obs.getBounds())) {
                    tank.setPosition(oldX, oldY);
                    break;
                }
            }

            for (SpawnBase base : spawnBases) {
                if (base.intersectsWall(tankBounds)) {
                    tank.setPosition(oldX, oldY);
                    break;
                }
            }

            // چک برخورد تیرها
            tank.getBullets().forEach(b -> {
                if (b.isActive()) {
                    Rectangle bulletBounds = new Rectangle((int)b.getX() - 3, (int)b.getY() - 3, 6, 6);

                    // برخورد با دیواره (حاشیه 20 پیکسل)
                    if (b.getX() < 20 || b.getX() > WIDTH - 20 ||
                            b.getY() < 20 || b.getY() > HEIGHT - 20) {
                        b.deactivate();
                    }

                    // برخورد با موانع
                    if (b.isActive()) {
                        for (Obstacle obs : obstacles) {
                            if (bulletBounds.intersects(obs.getBounds())) {
                                b.deactivate();
                                break;
                            }
                        }
                    }

                    // برخورد با دیوارهای SpawnBase
                    if (b.isActive()) {
                        for (SpawnBase base : spawnBases) {
                            if (base.intersectsWall(bulletBounds)) {
                                b.deactivate();
                                break;
                            }
                        }
                    }

                    // برخورد با تانک‌های دیگر
                    if (b.isActive()) {
                        for (Tank otherTank : tanks) {
                            if (otherTank != tank && otherTank.isAlive() &&
                                    !otherTank.isInvincible() &&
                                    bulletBounds.intersects(otherTank.getBounds())) {

                                otherTank.takeDamage();

                                if (!otherTank.isAlive()) {
                                    wrecks.add(new TankWreck(
                                            otherTank.getX(),
                                            otherTank.getY(),
                                            otherTank.getAngle(),
                                            otherTank.getColor()
                                    ));
                                }

                                b.deactivate();
                                break;
                            }

                        }
                    }
                }

                if (!b.isActive() && !b.isExplosionSpawned()) {
                    explosions.add(new Explosion(b.getX(), b.getY()));
                    b.markExplosionSpawned();
                }
            });
        });

        explosions.removeIf(ex -> !ex.isAlive());
        explosions.forEach(Explosion::update);

        repaint();
    }



    private void initSpawnBases() {
        int margin = 20;
        int clearZone = 50;
        int baseSize = 200;

        // بالا چپ
        spawnBases.add(new SpawnBase(
                margin + clearZone,
                margin + clearZone,
                "bottom"
        ));

        // پایین راست - اصلاح شده
        spawnBases.add(new SpawnBase(
                WIDTH - baseSize - margin - clearZone,
                HEIGHT - baseSize - margin - clearZone,
                "top"
        ));
    }



    private static class Explosion {
        private final double x, y;
        private int frame = 0;
        private static final int MAX_FRAMES = 18;
        private List<Particle> particles = new ArrayList<>();

        Explosion(double x, double y) {
            this.x = x;
            this.y = y;
            for (int i = 0; i < 12; i++) {
                double angle = Math.random() * 2 * Math.PI;
                double speed = 1.5 + Math.random() * 3.5;
                particles.add(new Particle(x, y, angle, speed));
            }
        }

        void update() {
            frame++;
            particles.forEach(Particle::update);}

        boolean isAlive() { return frame < MAX_FRAMES; }

        void draw(Graphics2D g2) {
            float progress = (float) frame / MAX_FRAMES;
            float alpha = 1f - progress;
            int size = (int)(30 * (1 - progress * 0.5));

            g2.setColor(new Color(1f, 0.6f - progress * 0.4f, 0f, alpha * 0.8f));
            g2.fillOval((int)(x - size / 2), (int)(y - size / 2), size, size);

            g2.setColor(new Color(1f, 1f, 0.5f, alpha * 0.6f));
            g2.fillOval((int)(x - size / 4), (int)(y - size / 4), size / 2, size / 2);

            particles.forEach(p -> p.draw(g2, alpha));
        }

        private static class Particle {
            double px, py, vx, vy;
            Color color;
            int size;

            Particle(double x, double y, double angle, double speed) {
                px = x; py = y;
                vx = Math.cos(angle) * speed;
                vy = Math.sin(angle) * speed;
                color = new Color(200 + (int)(Math.random() * 55), (int)(Math.random() * 150), 0);
                size = 3 + (int)(Math.random() * 4);
            }

            void update() { px += vx; py += vy; vy += 0.15; }

            void draw(Graphics2D g2, float alpha) {
                g2.setColor(new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha));
                g2.fillOval((int)px - size / 2, (int)py - size / 2, size, size);
            }
        }
    }
}
