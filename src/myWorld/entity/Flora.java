package myWorld.entity;

import myWorld.Level;

public class Flora extends Entity {
	String		foodType	= "plant";
	public int	foodCount;
	public int	maxFoodCount;
	public int	newHeight;
	public int	newWidth;

	/**
	 * Проверяет тайл, на котором находится этот объект. Если это растение и оно находится на камне или земле - возвращаем ложь . Этот метод необходим для удаления
	 * объектов, так что при вызове его удаляйте объекты, если ложь.
	 */
	public boolean checkPosition() {
		if (Level.map[x / 32][y / 32].getType() == 0 && foodType.equals("plant"))
			return true;
		return false;
	}

	/** Изменяет кол-во еды. Для уменьшения кол-во передавайте отрицательные числа.*/
	public void changeFoodCount(int food) {
		foodCount += food;
		if (foodCount > maxFoodCount)
			foodCount = maxFoodCount;
		if (foodCount <= 0) {
			Level.deleteEntity(name, x, y);
		}
	}

	@Override
	public void showStatus() {
		System.out.println("Name: " + name);
		System.out.println("Food Count: " + foodCount);
		System.out.println();
	}

	@Override
	/** Изменяет кол-во еды у куста. Для уменьшения подавайте отрицательное число*/
	public boolean action(int value) {
		changeFoodCount(value);
		return true;
	}
}
