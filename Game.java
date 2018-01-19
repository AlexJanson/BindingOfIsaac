import graphics.Screen;
import input.KeyBoard;
import level.Level;
import level.RandomLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {

    public static final long serialVersionUID = 1L;

    public static int width = 1368;
    public static int height = width /16 * 9;
    public static String title = "Binding of Isaac (Bad Remake) Java - ";

    private Thread thread;
    private JFrame frame;
    private KeyBoard key;
    private Level level;
    private boolean running = false;

    private Screen screen;

    private BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    public Game() {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        screen = new Screen(width,height);
        frame = new JFrame();
        key = new KeyBoard();
        level = new RandomLevel(64, 64);

        addKeyListener(key);
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        requestFocus();
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                update();
                updates++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " updates, " + frames + " FPS.");
                frame.setTitle(title + " " + updates + " updates, " + frames + " FPS.");
                frames = 0;
                updates = 0;
            }
        }
        stop();
    }

    int x = 0, y = 0;

    public void update() {
        key.update();
        if(key.up) y-=3;
        if(key.down) y+=3;
        if(key.left) x-=3;
        if(key.right) x+=3;
        if(key.esc) System.exit(1);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        screen.clear();
        level.render(x, y, screen);

        for(int i = 0; i < pixels.length; i++)
            pixels[i] = screen.pixels[i];

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.black);
        g.fillRect(0,0,getWidth(), getHeight());
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.frame.setResizable(false);
        game.frame.setTitle(title);
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);

        game.start();
    }
}
