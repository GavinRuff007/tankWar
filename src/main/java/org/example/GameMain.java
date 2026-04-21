package org.example;

import org.example.game.GamePanel;

import javax.swing.*;
import java.awt.*;

public class GameMain {

    private static CardLayout layout;
    private static JPanel root;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Tank Battle");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            layout = new CardLayout();
            root = new JPanel(layout);

            root.add(createStartMenu(), "menu");
            root.add(createPlayerCount(), "count");

            frame.add(root);
            frame.setVisible(true);
        });
    }

    // ================= START MENU =================

    private static JPanel createStartMenu() {

        JPanel panel = new JPanel() {

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0,0,new Color(10,20,40),
                        0,getHeight(),new Color(0,0,0));

                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());

                g2.setColor(new Color(255,255,255,40));
                for(int i=0;i<40;i++){
                    int x=(int)(Math.random()*getWidth());
                    int y=(int)(Math.random()*getHeight());
                    g2.fillOval(x,y,3,3);
                }

                g2.setFont(new Font("Arial",Font.BOLD,70));
                g2.setColor(Color.WHITE);
                g2.drawString("TANK BATTLE", getWidth()/2-220,150);
            }
        };

        panel.setLayout(new GridBagLayout());

        JButton play = new JButton("PLAY");

        play.setFont(new Font("Arial",Font.BOLD,40));
        play.setFocusPainted(false);
        play.setBackground(new Color(40,180,90));
        play.setForeground(Color.WHITE);

        play.addActionListener(e -> layout.show(root,"count"));

        panel.add(play);

        return panel;
    }

    // ================= PLAYER COUNT =================

    private static JPanel createPlayerCount() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(20,20,30));

        GridBagConstraints c = new GridBagConstraints();

        JLabel label = new JLabel("How many players?");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial",Font.BOLD,40));

        c.gridy=0;
        panel.add(label,c);

        Integer[] nums={1,2,3,4};
        JComboBox<Integer> box = new JComboBox<>(nums);
        box.setFont(new Font("Arial",Font.BOLD,30));

        c.gridy=1;
        panel.add(box,c);

        JButton next = new JButton("NEXT");
        next.setFont(new Font("Arial",Font.BOLD,30));

        c.gridy=2;
        panel.add(next,c);

        next.addActionListener(e->{
            int players=(int)box.getSelectedItem();
            root.add(createPlayerSetup(players),"setup");
            layout.show(root,"setup");
        });

        return panel;
    }

    // ================= PLAYER SETUP =================

    private static JPanel createPlayerSetup(int players) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30,30,40));

        Color[] colors = {
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW
        };

        JTextField[] names = new JTextField[players];
        JComboBox<String>[] teams = new JComboBox[players];

        String[] teamNames={
                "Red",
                "Blue",
                "Green",
                "Yellow"
        };

        for(int i=0;i<players;i++){

            JPanel row = new JPanel(new FlowLayout());
            row.setBackground(new Color(30,30,40));

            JLabel label = new JLabel("Player "+(i+1));
            label.setForeground(Color.WHITE);

            JTextField name = new JTextField("Player"+(i+1),10);
            names[i]=name;

            JComboBox<String> team = new JComboBox<>(teamNames);
            teams[i]=team;

            row.add(label);
            row.add(name);
            row.add(team);

            panel.add(row);
        }

        JButton start = new JButton("START GAME");
        start.setFont(new Font("Arial",Font.BOLD,30));

        start.addActionListener(e->{

            JFrame frame=(JFrame)SwingUtilities.getWindowAncestor(panel);

            frame.getContentPane().removeAll();

            GamePanel game = new GamePanel(players);
            frame.add(game);

            frame.revalidate();
            frame.repaint();

            game.requestFocusInWindow();
        });

        panel.add(Box.createVerticalStrut(40));
        panel.add(start);

        return panel;
    }
}
