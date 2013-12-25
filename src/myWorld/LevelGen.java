package myWorld;

import java.util.ArrayList;
import java.util.Random;

import myWorld.entity.Bush;
import myWorld.entity.Flora;
import myWorld.entity.Plant;
import myWorld.entity.Tile;

/**
 * Класс генерации мира. Создает мир, заполняет их тайлами и сущностями.
 * 
 * @author Epso
 */
public class LevelGen {
	Random	rnd	= new Random();
	int		rndX;
	int		rndY;

	/** Метод случайной генерации мира. Генерирует мир и растения на нём. */
	public void randomGen(Tile map[][], int w, int h, ArrayList<Flora> objectsFlora) {

		// генерация травы по всей карте
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++) {
				map[i][j] = new Tile(i, j, 0);
			}

		// Генерация одного кусочка земли:
		rndX = rnd.nextInt(Level.MAP_WIDTH / 32 - 8) + 4;
		rndY = rnd.nextInt(Level.MAP_HEIGHT / 32 - 8) + 4;
		for (int i = rndX - rnd.nextInt(4); i < rndX + rnd.nextInt(6) + 3; i++)
			for (int j = rndY - rnd.nextInt(4); j < rndY + rnd.nextInt(5) + 3; j++) {
				if (i < h && j < w)
					map[i][j] = new Tile(i, j, 1);
			}

		// Генерация двух кусочков камня
		for (int k = 1; k <= 2; k++) {
			rndX = rnd.nextInt(Level.MAP_WIDTH / 32 - 8) + 4;
			rndY = rnd.nextInt(Level.MAP_HEIGHT / 32 - 8) + 4;
			for (int i = rndX - rnd.nextInt(4); i < rndX + rnd.nextInt(6) + 3; i++)
				for (int j = rndY - rnd.nextInt(4); j < rndY + rnd.nextInt(5) + 3; j++) {
					if (i < h && j < w)
						map[i][j] = new Tile(i, j, 2);
				}
		}

		// Еще генерация одного кусочка земли
		for (int k = 1; k <= 2; k++) {
			rndX = rnd.nextInt(Level.MAP_HEIGHT / 32 - 8) + 4;
			rndY = rnd.nextInt(Level.MAP_WIDTH / 32 - 8) + 4;
			for (int i = rndX - rnd.nextInt(4); i < rndX + rnd.nextInt(6) + 3; i++)
				for (int j = rndY - rnd.nextInt(4); j < rndY + rnd.nextInt(5) + 3; j++) {
					if (i < w && j < h)
						map[j][i] = new Tile(i, j, 1);
				}
		}

		// Еще генерация трёх кусочков камня
		for (int k = 1; k <= 3; k++) {
			rndX = rnd.nextInt(Level.MAP_HEIGHT / 32 - 8) + 4;
			rndY = rnd.nextInt(Level.MAP_WIDTH / 32 - 8) + 4;
			for (int i = rndX - rnd.nextInt(4); i < rndX + rnd.nextInt(6) + 3; i++)
				for (int j = rndY - rnd.nextInt(4); j < rndY + rnd.nextInt(5) + 3; j++) {
					if (i < w && j < h)
						map[j][i] = new Tile(i, j, 2);
				}
		}

		// Создание объектов кустов:
		for (int i = 0; i < 150; i++) {
			rndX = rnd.nextInt(Level.MAP_WIDTH / 32) * 32;
			rndY = rnd.nextInt(Level.MAP_HEIGHT / 32) * 32;
			if (Level.objectOnPoint(rndX, rndY) == null) {
				objectsFlora.add(new Bush(rndX, rndY, 1.1));
			}
		}

		for (int i = 0; i < 100; i++) {
			rndX = rnd.nextInt(Level.MAP_WIDTH / 32) * 32;
			rndY = rnd.nextInt(Level.MAP_HEIGHT / 32) * 32;
			if (Level.objectOnPoint(rndX, rndY) == null) {
				objectsFlora.add(new Plant(rndX, rndY));
			}
		}
	}

	/** Держит кол-во растений. В день вырастает по 50 штук. Максимальное кол-во - 350 штук. Вызывать один раз в день. */
	public void addFlora(ArrayList<Flora> objectsFlora) {
		if (Level.objectsFlora.size() < 350) {
			for (int i = 0; i < 50; i++) {
				rndX = rnd.nextInt(Level.MAP_WIDTH / 32) * 32;
				rndY = rnd.nextInt(Level.MAP_HEIGHT / 32) * 32;
				if (Level.objectOnPoint(rndX, rndY) == null) {
					if (rnd.nextBoolean())
						objectsFlora.add(new Plant(rndX, rndY));
					else
						objectsFlora.add(new Bush(rndX, rndY, 1.1));
				}
			}
		}
	}
}
