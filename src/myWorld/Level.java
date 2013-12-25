package myWorld;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import myWorld.entity.Beast;
import myWorld.entity.Effect;
import myWorld.entity.Entity;
import myWorld.entity.Flora;
import myWorld.entity.Tile;

/**
 * Основной игровой класс, здесь содержатся отрисовка, игровой цикл, слушатели событий. Вызов обработки игровой логики у животных.
 * 
 * @author Epso
 * @version 0.1.0
 */
public class Level extends JPanel implements ActionListener, Runnable {
	private static final long		serialVersionUID	= 1L;

	JFrame							frame;

	LevelGen						levelGen;
	HUD								gui					= new HUD();

	/** Масштабы генерируемой карты. */
	public static final int			MAP_WIDTH			= 80 * 32;
	public static final int			MAP_HEIGHT			= 60 * 32;
	/** Размер экрана, который пользователь видит в данный момент. */
	public static int				CAMERA_HEIGHT		= MyWorld.GAME_HEIGHT;
	public static int				CAMERA_WIDTH		= MyWorld.GAME_WIDTH;
	private static boolean			isShowLine			= false;
	private static boolean			isDebugAI			= false;

	public static Tile				map[][]				= new Tile[MAP_WIDTH / 32][MAP_HEIGHT / 32];
	public static ArrayList<Flora>	objectsFlora		= new ArrayList<Flora>();
	public static ArrayList<Beast>	beasts				= new ArrayList<Beast>();
	public static ArrayList<Effect>	effects				= new ArrayList<Effect>();

	/** Максимальное кол-во вызовов метода перерисовки в секунду. */
	public static final int			MAX_FPS				= 60;
	/** Кол-во вызовов игрового цикла в секунду. */
	public static final int			GAME_SPEED			= 50;
	Timer							levelTimer			= new Timer(1000 / GAME_SPEED, this);
	public static int				curX				= 0;											// Текущие координаты камеры
	public static int				curY				= 0;
	public static int				mouseX				= CAMERA_WIDTH / 2;
	public static int				mouseY				= CAMERA_HEIGHT / 2;

	public static int				days				= 1;
	public static int				hours				= 8;
	private static int				timeCounter			= 0;
	/** Длительность игрового часа в секундах. */
	public static final int			hourTime			= 3;
	public static int				fps					= 0;
	public static int				ops					= 0;

	/** Выбранный пользователем текущий объект. */
	public static Entity			choosenObject		= null;
	/** Выбранный пользователем текущее животное. */
	public static Beast				choosenBeast		= null;

	/** Метод отрисовки. Вся графика рисуется здесь. */
	public void paint(Graphics g) {
		g = (Graphics2D) g;
		Beast curBeast;
		Flora curFlora;
		Effect curEffect;

		// Отрисовка тайлов карты.
		for (int i = curX, curCamX = 0; i < curX + CAMERA_WIDTH; i += 32, curCamX += 32)
			for (int j = curY, curCamY = 0; j < curY + CAMERA_HEIGHT; j += 32, curCamY += 32) {
				g.drawImage(map[(i % MAP_WIDTH / 32)][j % MAP_HEIGHT / 32].getImage(), curCamX, curCamY, 32, 32, null);
			}

		// После этого рисуются все объекты
		curFlora = null;
		for (int i = 0; i < objectsFlora.size(); i++) {
			curFlora = objectsFlora.get(i);
			if (curFlora.x >= curX && curFlora.x <= curX + CAMERA_WIDTH && curFlora.y >= curY && curFlora.y <= curY + CAMERA_HEIGHT) {
				g.drawImage(curFlora.getImage(), curFlora.x - curX, curFlora.y - curY, null);
				// TODO Лучше бы сделать отрисовку коллекций через итератор
			}
		}

		// Прорисовка зверей.
		curBeast = null;
		for (int i = 0; i < beasts.size(); i++) {
			curBeast = beasts.get(i);
			if (curBeast.x >= curX - 32 && curBeast.x <= curX + CAMERA_WIDTH && curBeast.y >= curY - 32 && curBeast.y <= curY + CAMERA_HEIGHT) {
				g.drawImage(curBeast.getImage(), curBeast.x - curX, curBeast.y - curY, null);
			}
		}

		// Отрисовка спец. эффектов:
		curEffect = null;
		for (int i = 0; i < effects.size(); i++) {
			curEffect = effects.get(i);
			if (curEffect.x >= curX && curEffect.x <= curX + CAMERA_WIDTH && curEffect.y >= curY && curEffect.y <= curY + CAMERA_HEIGHT) {
				g.drawImage(curEffect.getImage(), curEffect.x - curX, curEffect.y - curY, null);
			}
		}

		// Добавление отрисовки освещенности:
		g.setColor(new Color(25, 25, 25, Math.abs((hours - 12) * 4)));
		g.fillRect(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		g.setColor(Color.BLACK);

		// Отрисовка линий, если они включены
		if (isShowLine) {
			g.setColor(Color.GRAY);
			for (int i = 1; i <= CAMERA_WIDTH / 32; i++) {
				g.drawLine(32 * i, 0, 32 * i, CAMERA_HEIGHT);
			}
			for (int i = 1; i <= CAMERA_HEIGHT / 32; i++) {
				g.drawLine(0, 32 * i, CAMERA_WIDTH, 32 * i);
			}
		}

		// Отрисовка линий от животных до их целей.
		if (isDebugAI) {
			curBeast = null;
			for (int i = 0; i < beasts.size(); i++) {
				curBeast = beasts.get(i);

				g.setColor(Color.BLUE);
				if (curBeast.target != null) {
					if (curBeast.x >= curX && curBeast.x <= curX + CAMERA_WIDTH && curBeast.y >= curY && curBeast.y <= curY + CAMERA_HEIGHT) {
						g.drawLine(curBeast.x + curBeast.size / 2 - curX, curBeast.y + curBeast.size / 2 - curY, curBeast.target.x + curBeast.target.getImageWidth() / 2
								- curX, curBeast.target.y + curBeast.target.getImageWidth() / 2 - curY);
					}
				}
				g.setColor(Color.CYAN);
				if (curBeast.targetBeast != null) {
					if (curBeast.x >= curX && curBeast.x <= curX + CAMERA_WIDTH && curBeast.y >= curY && curBeast.y <= curY + CAMERA_HEIGHT) {
						g.drawLine(curBeast.x + curBeast.size / 2 - curX, curBeast.y + curBeast.size / 2 - curY, curBeast.targetBeast.x + curBeast.targetBeast.size / 2
								- curX, curBeast.targetBeast.y + curBeast.targetBeast.size / 2 - curY);
					}
				}
			}
		}

		// Отрисовка выделения объектов:
		g.setColor(Color.ORANGE);
		if (choosenObject != null)
			g.drawOval((choosenObject.x - curX), (choosenObject.y - curY), choosenObject.getImageWidth(), choosenObject.getImageWidth());
		g.setColor(Color.BLACK);

		// Отрисовка всего худа.
		gui.paintHUD(g);

		// Когда отрисовали, увеличиваем наш счётчик фпс.
		fps++;
	}

	/**
	 * Внутренний класс, на который мы вешаем слушателей и тут же реализуем нужный отклик на действия. Здесь собрано все управление. Была попытка переделать это без
	 * внутреннего класса, но с ним решение оказалось лучше.
	 * 
	 * @author Epso
	 */
	private class MyControlAdapter extends KeyAdapter implements MouseMotionListener, MouseListener {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
				Level.moveCamera(32, 0, true);
			}
			if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
				Level.moveCamera(-32, 0, true);
			}
			if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
				Level.moveCamera(0, -32, true);
			}
			if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
				Level.moveCamera(0, 32, true);
			}
			if (key == KeyEvent.VK_L) {
				Level.isShowLine = !(Level.isShowLine);
			}
			if (key == KeyEvent.VK_O) {
				Level.isDebugAI = !(Level.isDebugAI);
			}
			if (key == KeyEvent.VK_Q) {
				beasts.add(new Beast(Beast.TYPE_BUNNY, mouseX + curX, mouseY + curY));
			}
			if (key == KeyEvent.VK_T) {
				for (int i = 0; i < 50; i++) {
					beasts.add(new Beast(Beast.TYPE_BUNNY, mouseX + curX + MyWorld.rnd.nextInt(320) - 160, mouseY + curY + MyWorld.rnd.nextInt(320) - 160));
				}
			}
			if (key == KeyEvent.VK_J) {
				Level.effects.add(new Effect(Effect.EFFECT_BORN, mouseX + curX, mouseY + curY, 24));
			}
			if (key == KeyEvent.VK_G) {
				beasts.add(new Beast(Beast.TYPE_WOLF, mouseX + curX, mouseY + curY));
			}

			if (key == KeyEvent.VK_I) {
				if (choosenObject != null) {
					choosenObject.showStatus();
				}
			}
			if (key == KeyEvent.VK_E) {
				if (choosenObject != null) {
					for (int i = 0; i < beasts.size(); i++) {
						if (beasts.get(i).x == choosenObject.x && beasts.get(i).y == choosenObject.y) {
							beasts.remove(i);
							choosenObject = null;
							return;
						}
					}
					for (int i = 0; i < objectsFlora.size(); i++) {
						if (objectsFlora.get(i).x == choosenObject.x && objectsFlora.get(i).y == choosenObject.y) {
							objectsFlora.remove(i);
							choosenObject = null;
							return;
						}
					}
				}
			}
			if (key == KeyEvent.VK_R) {
				for (int i = 0; i < beasts.size(); i++)
					beasts.remove(i);
			}
			if (key == KeyEvent.VK_SPACE) {
				if (choosenObject != null)
					moveCamera(choosenObject.x, choosenObject.y, false);
			}
			if (key == KeyEvent.VK_EQUALS) {
				if (curX + CAMERA_WIDTH < MAP_WIDTH && curY + CAMERA_HEIGHT < MAP_HEIGHT) {
					CAMERA_WIDTH += 64;
					CAMERA_HEIGHT += 64;
				}
				frame.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
			}
			if (key == KeyEvent.VK_MINUS) {
				if (CAMERA_WIDTH > 20 * 32 && CAMERA_HEIGHT > 15 * 32) {
					CAMERA_WIDTH -= 64;
					CAMERA_HEIGHT -= 64;
				}
				frame.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (e.getX() <= CAMERA_WIDTH)
				mouseX = e.getX();
			if (e.getY() <= CAMERA_HEIGHT)
				mouseY = e.getY();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if ((choosenObject = beastOnPoint(curX + mouseX, curY + mouseY)) == null) {
					choosenObject = objectOnPoint(curX + mouseX, curY + mouseY);
					choosenBeast = null;
				}
				else {
					choosenBeast = beastOnPoint(curX + mouseX, curY + mouseY);
				}
			}

			if (e.getButton() == MouseEvent.BUTTON3) {
				choosenBeast.setDirection(Beast.getAngle(choosenBeast.x, choosenBeast.y, mouseX + curX, mouseY + curY));
			}

			if (e.getButton() == MouseEvent.BUTTON2) {
				map[(curX + mouseX) / 32][(curY + mouseY) / 32].nextType();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	/** Метод игровой логики. Вызывается для обработки игровых данных. */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Убираем худ вниз при наведении на него мышкой
		if ((mouseX <= (32 * (gui.HUDLength + 1)) || (mouseX >= CAMERA_WIDTH - (32 * (gui.HUDLength + 1)))) && mouseY <= 64)
			gui.correctHUD = CAMERA_HEIGHT - (2 * (HUD.HUD_Y + 32));
		else
			gui.correctHUD = 0;
		if (choosenObject != null) {
			if (!isExist(choosenObject.getName(), choosenObject.x, choosenObject.y)) {
				choosenObject = null;
				choosenBeast = null;
			}
		}

		// Считаем часы и дни
		timeCounter++;
		if (timeCounter >= hourTime * GAME_SPEED) {
			// Новый час
			timeCounter %= hourTime * GAME_SPEED;
			hours++;
			if (hours >= 24) {
				// Новый день
				days++;
				hours %= 24;

				levelGen.addFlora(objectsFlora);

				// Проверяем возможность нахождения растений на месте, где они стоят:
				// Т.к. во время удаления размер уменьшается, не все сразу растения будут убираться, а по порциям. "Это не баг, это фича"
				for (int i = 0; i < objectsFlora.size(); i++) {
					objectsFlora.get(i).changeFoodCount(10);
					if (!objectsFlora.get(i).checkPosition()) {
						if (choosenObject == objectsFlora.get(i))
							choosenObject = null; // Так же убираем курсор
						objectsFlora.remove(i);
					}
				}
			}
		}

		// Обновление животных:
		for (int i = 0; i < beasts.size(); i++) {
			// beasts.get(i).updateImage();
			// beasts.get(i).updatePos();
			// beasts.get(i).updateState();
			beasts.get(i).update();
		}

		// Обновение эффектов:
		for (int i = 0; i < effects.size(); i++) {
			effects.get(i).updateImage();
		}

		ops++;
	}

	/**
	 * Указать, куда сдвинуть камеру,
	 * @param relative
	 *            - если истина, сдвигает относительно текущего значения. Если нет - позиционирует камеру с центром в xx, yy или максимально близко, если точка у краёв
	 *            карты.
	 */
	public static void moveCamera(int xx, int yy, boolean relative) {
		if (relative) {
			if (xx >= 0) {
				if (curX + CAMERA_WIDTH + xx <= MAP_WIDTH)
					curX += xx;
			}
			else {
				if (curX - Math.abs(xx) >= 0)
					curX -= Math.abs(xx);
			}

			if (yy >= 0) {
				if (curY + CAMERA_HEIGHT + yy <= MAP_HEIGHT)
					curY += yy;
			}
			else {
				if (curY - Math.abs(yy) >= 0)
					curY -= Math.abs(yy);
			}
		}
		else { // Переход по абсолютным координатам карты:
			if (xx >= 0 && xx <= (MAP_WIDTH) && yy >= 0 && yy <= (MAP_HEIGHT)) {
				curX = (xx / 32) * 32 - CAMERA_WIDTH / 2 + 64;
				curY = (yy / 32) * 32 - CAMERA_HEIGHT / 2 + 64;
				// Если не получается поставить объект по центру, приближаемся к нему, как можем
				if (curX < 0)
					curX = 0;
				if (curY < 0)
					curY = 0;
				if (curX + CAMERA_WIDTH > MAP_WIDTH)
					curX = MAP_WIDTH - CAMERA_WIDTH;
				if (curY + CAMERA_HEIGHT > MAP_HEIGHT)
					curY = MAP_HEIGHT - CAMERA_HEIGHT;
			}
		}
	}

	/** Возвращает объект в клетке с абсолютными координатами x,y */
	public static Entity objectOnPoint(int x, int y) {
		int mapX = (x / 32) * 32;
		int mapY = (y / 32) * 32;
		Flora curFlora;
		for (int i = 0; i < objectsFlora.size(); i++) {
			curFlora = objectsFlora.get(i);
			if (curFlora.x == mapX && curFlora.y == mapY) { return curFlora; }
		}
		return null;
	}

	/** Возвращает животное, находящееся в клетке мира x, y */
	public static Beast beastOnPoint(int x, int y) {
		Beast curBeast;
		for (int i = 0; i < beasts.size(); i++) {
			curBeast = beasts.get(i);
			if (x >= curBeast.x && x <= curBeast.x + (curBeast.size + 8) && y >= curBeast.y && y <= curBeast.y + (curBeast.size) + 8) { return curBeast; }
		}
		return null;
	}

	/** Возвращает животное, находящееся по координатам x, y */
	public static Beast beastOnPointAbs(int x, int y, String name) {
		Beast curBeast;
		for (int i = 0; i < beasts.size(); i++) {
			curBeast = beasts.get(i);
			if (curBeast.x == x && curBeast.y == y && curBeast.getName().equalsIgnoreCase(name)) { return curBeast; }
		}
		return null;
	}

	/** Удаляет объект, который совпадет с параметрами x, y и именем.*/
	public static void deleteEntity(String name, int x, int y) {
		Effect curEffect;
		if (name.equalsIgnoreCase("EFFECT")) {
			for (int i = 0; i < effects.size(); i++) {
				curEffect = effects.get(i);
				if (curEffect.x == x && curEffect.y == y) {
					effects.remove(i);
					curEffect = null;
					return;
				}
			}
		}

		Entity curEntity;
		for (int i = 0; i < objectsFlora.size(); i++) {
			curEntity = objectsFlora.get(i);
			if (curEntity.x == x && curEntity.y == y && curEntity.getName().equalsIgnoreCase(name)) {
				objectsFlora.remove(i);
				curEntity = null;
				return;
			}
		}

		for (int i = 0; i < beasts.size(); i++) {
			curEntity = beasts.get(i);
			if (curEntity.x == x && curEntity.y == y && curEntity.getName().equalsIgnoreCase(name)) {
				beasts.remove(i);
				curEntity = null;
				return;
			}
		}
	}

	/** Возвращает истину, если найдет какой либо объект с координатами x, y и именем name*/
	public static boolean isExist(String name, int x, int y) {
		Entity curEntity;
		for (int i = 0; i < objectsFlora.size(); i++) {
			curEntity = objectsFlora.get(i);
			if (curEntity.x == x && curEntity.y == y && curEntity.getName().equalsIgnoreCase(name)) { return true; }
		}

		for (int i = 0; i < beasts.size(); i++) {
			curEntity = beasts.get(i);
			if (curEntity.x == x && curEntity.y == y && curEntity.getName().equalsIgnoreCase(name)) { return true; }
		}

		for (int i = 0; i < effects.size(); i++) {
			curEntity = effects.get(i);
			if (curEntity.x == x && curEntity.y == y && curEntity.getName().equalsIgnoreCase(name)) { return true; }
		}

		return false;
	}

	/** Начальная инициализация уровня при помещении его на наше игровое окно.*/
	public Level(JFrame frame) {
		this.frame = frame;

		levelGen = new LevelGen();
		levelGen.randomGen(map, MAP_HEIGHT / 32, MAP_WIDTH / 32, objectsFlora);

		MyControlAdapter myControlAdapter = new MyControlAdapter();
		addKeyListener(myControlAdapter);
		addMouseMotionListener(myControlAdapter);
		addMouseListener(myControlAdapter);
		
		setFocusable(true);
		levelTimer.start();
		new Thread(this).start();
	}

	/** Метод запущенного потока. Вынесен отдельно что бы отрисовка производилась асинхронно от игровой обработки данных. */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000 / MAX_FPS);
				repaint();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
