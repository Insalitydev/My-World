package myWorld;

import java.awt.Dimension;
import java.util.Random;

import javax.swing.JFrame;

/**
 * Класс для начальной инициализации и создания игрового окна. Основной класс приложения.
 * @author Epso
 * @version 0.1.0
 */
public class MyWorld implements Runnable {
	public static final int		GAME_WIDTH	= 40 * 32;
	public static final int		GAME_HEIGHT	= 30 * 32;
	// public static final int SCREEN_SCALE = 4;
	public static final Random	rnd			= new Random();
	static MyWorld				myWorld;
	static boolean				isShowFPS	= true;

	public static void main(String[] args) {
		JFrame frame = new JFrame("My World");

		myWorld = new MyWorld();

		Dimension d = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		frame.pack();
		frame.setResizable(false);
		frame.setPreferredSize(d);
		frame.setSize(d);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Level(frame));

		new Thread(myWorld).start();
		frame.setVisible(true);
	}

	@Override
	/** Метод, вынесенный в отдельный поток для подсчета нашего фпс и опс.*/
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
				if (isShowFPS)
					System.out.println("FPS " + Level.fps + " OPS: " + Level.ops + " Creatures: " + Level.beasts.size() + " Flora: " + Level.objectsFlora.size());
				Level.fps = 0;
				Level.ops = 0;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
