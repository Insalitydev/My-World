package myWorld.entity;

import java.awt.Image;

/**
 * Класс, от которого наследуются все объекты и животные. Содержит стандартные поля и методы для всех объектов.
 * 
 * @author Epso
 * @version 0.4.0
 * @see myWorld.entity.Beast#findFood()
 */
public abstract class Entity {
	/** Абсолютная координата x в игровом мире */
	public int			x;
	/** Абсолютная координата y в игровом мире */
	public int			y;
	protected Image		image;
	protected String	name;
	
	

	/** Совершает с объектом стандартное для него действие. Необходимо переопределить для каждого типа сущности.*/
	public abstract boolean action(int value);

	/** Выводит все параметры на консоль */
	public abstract void showStatus();

	/**
	 * Возвращает ссылку на текущую картинку объекта
	 * 
	 * @return Ссылка на изобращения типа Image;
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Устанавливает текущее изображение объекта на передаваемое.
	 * 
	 * @param image
	 *            Новое желаемое изображение объекта;
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * Возвращает имя объекта.
	 * 
	 * @return Имя объекта типа String;
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает имя объекта
	 * 
	 * @param name
	 *            Новое желаемое имя объекта.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает ширину текущего изображения объекта.
	 * 
	 * @return Ширина изображения в пикселях.
	 */
	public int getImageWidth() {
		return image.getWidth(null);
	}
}
