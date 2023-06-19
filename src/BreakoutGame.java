import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BreakoutGame extends JPanel implements ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_DIAMETER = 30;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int INITIAL_BRICK_ROWS = 3;
    private static final int INITIAL_BRICK_COLS = 8;
    private static final int TIMER_DELAY = 5;
    

    private Timer timer;
    private int paddleX;
    private int ballX;
    private int ballY;
    private int ballXSpeed;
    private int ballYSpeed;
    private boolean ballFalling;
    private boolean gameOver;
    private int bricksLeft;
    private int brickRows;
    private int brickCols;
    private int ballSpeed = 4; // Faster ball speed

    private boolean[][] bricks;

    public BreakoutGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2 - BALL_DIAMETER / 2;
        ballY = HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER - 1;
        ballXSpeed = ballSpeed; // Faster ball speed
        ballYSpeed = ballSpeed; // Faster ball speed
        ballFalling = false;
        gameOver = false;
        brickRows = INITIAL_BRICK_ROWS;
        brickCols = INITIAL_BRICK_COLS;

        bricks = new boolean[brickRows][brickCols];
        bricksLeft = bricks.length * bricks[0].length;

        initializeBricks();

        timer = new Timer(TIMER_DELAY, this);
        timer.start();

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!gameOver) {
                    paddleX = e.getX() - PADDLE_WIDTH / 2;
                    if (paddleX < 0) paddleX = 0;
                    if (paddleX > WIDTH - PADDLE_WIDTH) paddleX = WIDTH - PADDLE_WIDTH;
                }
            }
        });
    }

    private void initializeBricks() {
        for (int row = 0; row < brickRows; row++) {
            for (int col = 0; col < brickCols; col++) {
                bricks[row][col] = true;
            }
        }
    }

    private void moveBall() {
        ballX += ballXSpeed;
        ballY += ballYSpeed;

        if (ballX <= 0 || ballX >= WIDTH - BALL_DIAMETER)
            ballXSpeed = -ballXSpeed;

        if (ballY <= 0) ballYSpeed = -ballYSpeed;

        if (ballY >= HEIGHT - BALL_DIAMETER) {
            gameOver = true;
            timer.stop();
        }

        if (ballY >= HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER && ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballYSpeed = -ballYSpeed;
            ballFalling = false;
        }

        int row = ballY / BRICK_HEIGHT;
        int col = ballX / BRICK_WIDTH;
        if (row >= 0 && row < bricks.length && col >= 0 && col < bricks[row].length && bricks[row][col]) {
            bricks[row][col] = false;
            ballYSpeed = -ballYSpeed;
            ballFalling = true;
            bricksLeft--;
            if (bricksLeft == 0) {
                increaseDifficulty();
            }
        }
    }

    private void increaseDifficulty() {
        brickRows++;
        brickCols++;
        ballSpeed += 2;
        bricks = new boolean[brickRows][brickCols];
        bricksLeft = bricks.length * bricks[0].length;
        initializeBricks();
        resetBallAndPaddle();
    }

    private void resetBallAndPaddle() {
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2 - BALL_DIAMETER / 2;
        ballY = HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER - 1;
        ballXSpeed = ballSpeed; // Faster ball speed
        ballYSpeed = -ballSpeed; // Faster ball speed
        ballFalling = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);

        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);

        g.setColor(Color.GREEN);
        for (int row = 0; row < bricks.length; row++) {
            for (int col = 0; col < bricks[row].length; col++) {
                if (bricks[row][col]) {
                    int brickX = col * BRICK_WIDTH;
                    int brickY = row * BRICK_HEIGHT;
                    g.fillRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
                }
            }
        }

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String message = "Game Over";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, WIDTH / 2 - messageWidth / 2, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveBall();
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().add(new BreakoutGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
