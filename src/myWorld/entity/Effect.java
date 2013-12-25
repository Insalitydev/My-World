package myWorld.entity;

import myWorld.Art;
import myWorld.Level;

public class Effect extends Entity {

	public byte			type;
	public static byte	EFFECT_SLEEP		= 4;
	public static byte	EFFECT_HEART		= 5;
	public static byte	EFFECT_BLOOD		= 6;
	public static byte	EFFECT_BORN			= 7;

	public int			curLifeTime			= 0;
	public int			lifeTime;
	public int			updateImage;
	public int			updateImageCounter	= 0;
	public byte			currentSprite		= 0;
	public byte			spriteAmount;

	/**
	 * Создает спец. эффект по координатам (x,y). Тип задается константно: EFFECT_*.
	 * 
	 * @param lifeTime
	 *            - Кол-во смен анимации эффекта, после чего будет удалён.
	 */
	public Effect(byte type, int x, int y, int lifeTime) {
		this.x = x;
		this.y = y;
		this.type = type;
		setName("EFFECT");
		this.lifeTime = lifeTime;
		if (type == EFFECT_HEART || type == EFFECT_SLEEP) {
			if (type == EFFECT_HEART)
				updateImage = 15;
			if (type == EFFECT_SLEEP)
				updateImage = 20;
			spriteAmount = 2;
			setImage(Art.objects[currentSprite][type]);
		}

		if (type == EFFECT_BLOOD) {
			updateImage = 15;
			spriteAmount = 1;
			setImage(Art.objects[currentSprite][type]);
		}

		if (type == EFFECT_BORN) {
			updateImage = 2;
			spriteAmount = 1;
			setImage(Art.objects[currentSprite][type]);
		}

	}

	/** Тут вся логика по изменению анимации у спец. эффектов. */
	public void updateImage() {
		updateImageCounter++;
		if (updateImageCounter >= updateImage) {
			updateImageCounter %= updateImage;

			currentSprite++;
			currentSprite %= spriteAmount;

			curLifeTime++;
			if (curLifeTime > lifeTime) {
				Level.deleteEntity(getName(), this.x, this.y);
			}

			if (type == EFFECT_BORN) {
				this.y -= 1;
			}

			setImage(Art.objects[currentSprite][type]);

		}
	}

	@Override
	public boolean action(int value) {
		return false;
	}

	@Override
	public void showStatus() {
	}

}
