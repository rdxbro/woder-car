import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

// Car class to define properties
class Car {
    String name;
    int speed;
    int power;

    Car(String name, int speed, int power) {
        this.name = name;
        this.speed = speed;
        this.power = power;
    }
}

public class AdvancedCarRacingGameWithModes extends JPanel implements ActionListener, KeyListener {
    private final int WIDTH = 400;
    private final int HEIGHT = 600;
    private Timer timer;
    private int playerX = WIDTH / 2 - 25;
    private int playerY = HEIGHT - 100;
    private int playerSpeed;
    private int jumpHeight = 0;
    private boolean isJumping = false;
    private int enemySpeed = 5;
    private int rampSpeed = 5;
    private int score = 0;
    private ArrayList<Rectangle> enemies;
    private ArrayList<Rectangle> ramps;
    private Image backgroundImage;
    private int bgY1 = 0, bgY2 = -HEIGHT;
    private Car selectedCar;
    private String gameMode;
    private boolean isGameOver = false;

    // Constructor with Game Mode
    public AdvancedCarRacingGameWithModes(Car selectedCar, String gameMode) {
        this.selectedCar = selectedCar;
        this.gameMode = gameMode;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        enemies = new ArrayList<>();
        ramps = new ArrayList<>();
        generateEnemyCars();
        generateRamps();

        // Load city background image
        backgroundImage = new ImageIcon("city_background.jpg").getImage();

        timer = new Timer(30, this);
        timer.start();
    }

    // Generates enemy cars
    private void generateEnemyCars() {
        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            int x = rand.nextInt(WIDTH - 50);
            int y = -rand.nextInt(600);
            enemies.add(new Rectangle(x, y, 50, 100));
        }
    }

    // Generates ramps
    private void generateRamps() {
        Random rand = new Random();
        for (int i = 0; i < 2; i++) {
            int x = rand.nextInt(WIDTH - 100);
            int y = -rand.nextInt(600);
            ramps.add(new Rectangle(x, y, 100, 20));
        }
    }

    // Action performed on each timer tick
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameMode.equals("Race")) {
            raceMode();
        } else if (gameMode.equals("Survival")) {
            survivalMode();
        } else if (gameMode.equals("Challenge")) {
            challengeMode();
        }
        moveEnemies();
        moveRamps();
        moveBackground();
        checkCollisions();
        repaint();
    }

    // Race Mode Logic
    private void raceMode() {
        score++;
        if (score > 100) {  // Assume player completes race after reaching score of 100
            timer.stop();
            JOptionPane.showMessageDialog(this, "Race Complete! Your Score: " + score);
            System.exit(0);
        }
    }

    // Survival Mode Logic
    private void survivalMode() {
        if (score > 50) {
            enemySpeed = 7; // Increase enemy speed
            rampSpeed = 6; // Increase ramp speed
        }
    }

    // Challenge Mode Logic
    private void challengeMode() {
        if (score > 20) {
            JOptionPane.showMessageDialog(this, "Challenge Completed! Your Score: " + score);
            timer.stop();
            System.exit(0);
        }
    }

    // Move background for scrolling effect
    private void moveBackground() {
        bgY1 += 5;
        bgY2 += 5;
        if (bgY1 >= HEIGHT) bgY1 = -HEIGHT;
        if (bgY2 >= HEIGHT) bgY2 = -HEIGHT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw city background
        g.drawImage(backgroundImage, 0, bgY1, WIDTH, HEIGHT, this);
        g.drawImage(backgroundImage, 0, bgY2, WIDTH, HEIGHT, this);

        // Draw player car
        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY - jumpHeight, 50, 100);

        // Draw enemy cars
        g.setColor(Color.RED);
        for (Rectangle enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }

        // Draw ramps
        g.setColor(Color.ORANGE);
        for (Rectangle ramp : ramps) {
            g.fillRect(ramp.x, ramp.y, ramp.width, ramp.height);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);

        // Game Over if any condition met
        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.drawString("Game Over! Final Score: " + score, WIDTH / 4, HEIGHT / 2);
        }
    }

    // Handle collisions
    private void checkCollisions() {
        for (Rectangle enemy : enemies) {
            if (new Rectangle(playerX, playerY - jumpHeight, 50, 100).intersects(enemy)) {
                isGameOver = true;
                timer.stop();
                JOptionPane.showMessageDialog(this, "Game Over! Your Score: " + score);
                System.exit(0);
            }
        }

        for (Rectangle ramp : ramps) {
            if (new Rectangle(playerX, playerY - jumpHeight, 50, 100).intersects(ramp)) {
                isJumping = true;
                jumpHeight = selectedCar.power;
            }
        }
    }

    // KeyPressed to handle W, A, S, D controls for Forward, Left, Backward, Right
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A && playerX > 0) playerX -= selectedCar.speed;  // Move Left (A)
        if (key == KeyEvent.VK_D && playerX < WIDTH - 50) playerX += selectedCar.speed;  // Move Right (D)
        if (key == KeyEvent.VK_W && playerY > 0) playerY -= selectedCar.speed;  // Move Up (W)
        if (key == KeyEvent.VK_S && playerY < HEIGHT - 100) playerY += selectedCar.speed;  // Move Down (S)
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Main method with game mode selection
    public static void main(String[] args) {
        // Car options
        Car car1 = new Car("Speedster", 10, 15);
        Car car2 = new Car("Tank", 5, 30);
        Car car3 = new Car("Racer", 15, 10);

        // Game Mode selection
        String[] modes = {"Race", "Survival", "Challenge"};
        String selectedMode = (String) JOptionPane.showInputDialog(
                null,
                "Choose Game Mode:",
                "Game Mode Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                modes,
                modes[0]
        );

        // Car selection
        Object[] carOptions = {car1.name, car2.name, car3.name};
        String selectedCarName = (String) JOptionPane.showInputDialog(
                null,
                "Choose your car:",
                "Car Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                carOptions,
                car1.name
        );

        Car selectedCar = car1;  // Default
        if (selectedCarName.equals(car2.name)) selectedCar = car2;
        if (selectedCarName.equals(car3.name)) selectedCar = car3;

        JFrame frame = new JFrame("Advanced Car Racing Game with Modes");
        AdvancedCarRacingGameWithModes gamePanel = new AdvancedCarRacingGameWithModes(selectedCar, selectedMode);

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
