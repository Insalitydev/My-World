package myWorld.entity;

import java.awt.Image;

import myWorld.Art;
import myWorld.MyWorld;

public class Bush extends Flora {

	public Bush(int x, int y) {
		bush(x, y);
	}

	/** Создает куст по координатам x,y и измененым размером на scale. */
	public Bush(int x, int y, double scale) {
		bush(x, y);

		newWidth = (int) (this.image.getWidth(null) * scale);
		newHeight = (int) (this.image.getHeight(null) * scale);

		this.image = this.image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
	}

	@Override
	public void changeFoodCount(int food) {
		super.changeFoodCount(food);
		double scaleChanged;
		if (foodCount >= 40)
			scaleChanged = 1.1;
		else if (foodCount >= 20)
			scaleChanged = 1.0;
		else
			scaleChanged = 0.9;

		newWidth = (int) (Art.objects[0][0].getWidth(null) * scaleChanged);
		newHeight = (int) (Art.objects[0][0].getHeight(null) * scaleChanged);
		this.image = this.image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
	}

	private void bush(int x, int y) {
		this.x = x;
		this.y = y;
		this.foodCount = MyWorld.rnd.nextInt(20) + 40;
		this.maxFoodCount = 60;

		this.name = "BUSH";
		this.image = Art.objects[0][0];

	}
}
