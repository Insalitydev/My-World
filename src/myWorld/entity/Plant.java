package myWorld.entity;

import java.util.Random;

import myWorld.Art;
import myWorld.Level;

public class Plant extends Flora {
	static private Random	rnd				= new Random();
	public static byte		AMOUNT_OF_TILES	= 3;
	int						tileType;

	public Plant(int x, int y) {
		this.x = x;
		this.y = y;
		maxFoodCount = 40;
		tileType = rnd.nextInt(3);
		foodCount = 30 - 10 * tileType;

		this.name = "PLANT";
		this.image = Art.objects[tileType][1];
	}

	@Override
	public void changeFoodCount(int food) {
		super.changeFoodCount(food);
		tileType = foodCount / 10;
		if (tileType >= AMOUNT_OF_TILES)
			tileType = (AMOUNT_OF_TILES - 1);
		tileType = AMOUNT_OF_TILES - tileType - 1;
		image = Art.objects[tileType][1];
	}

	@Override
	public boolean checkPosition() {
		if (Level.map[x / 32][y / 32].getType() == 0 && foodType.equals("plant")) {
			return true;
		}
		else if (tileType < (AMOUNT_OF_TILES - 1)) {
			tileType++;
			foodCount -= 10;
			image = Art.objects[tileType][1];
			return true;
		}
		else
			return false;
	}

}
