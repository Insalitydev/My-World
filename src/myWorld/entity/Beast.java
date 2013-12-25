package myWorld.entity;

import myWorld.Art;
import myWorld.Level;
import myWorld.MyWorld;

/**
 * Класс игрового существа. Здесь находится обработка ИИ животных, их анимация и передвижения.
 * 
 * @author Epso
 * @version 0.0.2
 */
public class Beast extends Entity {
	public final static int		AMOUNT_OF_SPRITE		= 6;								// Кол-во спрайтов в анимации ходьбы.
	public final static int		MAP_BORDER				= 2 * 32;							// ко скольки клеткам от краёв карты не доступно подходить животным. Сделано
																							// бесполезным

	/** Тип существа. Задавать константами TYPE_* */
	public byte					beastType;
	private byte				spriteRow;
	public final static byte	TYPE_BUNNY				= 0;
	public final static byte	TYPE_WOLF				= 1;
	public final static byte	TYPE_RAVEN				= 2;

	/** Тип питания существа. Задавать константами: PLANT_EATING, PREDATOR, SCAVENGER. */
	public byte					feedType;
	public final static byte	PLANT_EATING			= 0;
	public final static byte	PREDATOR				= 1;
	public final static byte	OMNIVOROUS				= 2;
	public final static byte	SCAVENGER				= 3;

	/** Текущее состояние животного. Задавать константами STATE_* */
	public byte					state					= 0;
	/** Сколько шагов уже идет текущее состояние. Обнуляется при смене состояния. */
	public int					stateStep				= 0;
	public final static byte	STATE_WALKING			= 0;
	public final static byte	STATE_FINDING_FOOD		= 1;
	public final static byte	STATE_EAT				= 2;
	public final static byte	STATE_RUN_OUT			= 3;
	public final static byte	STATE_SLEEP				= 4;
	public final static byte	STATE_FINDING_REPROD	= 5;
	public final static byte	STATE_REPRODUCTION		= 6;
	public final static byte	STATE_DEAD				= 7;

	static EnumStimulus			stimulusType[]			= EnumStimulus.values();
	/** Массив, который хранит все основные параметры животного: голод, страх, усталость и т.д. */
	public double[]				stimulus				= new double[stimulusType.length];

	/** Почасовое изменение параметров, насколько изменяется каждый параметр. */
	private double				deltaHunger;
	private double				deltaExhaust;
	private double				deltaCareful;
	private double				deltaAggression;
	private double				deltaFear;
	private double				deltaReproduction;

	// Parameters
	public int					HP;
	public int					MaxHP;
	public int					walkSpeed;
	public int					runSpeed;
	public boolean				isRun;
	public boolean				isStay;
	public int					direction;
	public int					distanceOfSight;
	public boolean				live;														// жив ли?
	private int					hungryDeathCounter;
	public int					timeOfCorpse;												// Сколько шагов гниет труп
	public int					corpseFoodValue;											// Сколько еды с него получат хищники и падальщики
	public boolean				male;														// пол
	public boolean				isPregnant				= false;							// Беременна ли?
	public int					timeOfPregnant;
	private int					pregnantTimeCounter		= 0;
	public int					size;														// размер животного (грубо считаем что круг)
	private int					thinkTime;													// через какой промежуток времени существо выполняет действия текущего
																							// состояния.

	/** Цель существа. Может быть каким-либо объектом */
	public Entity				target;
	/** Цель существа - другое существо. Сделано для доступа к методам выбранного существа */
	public Beast				targetBeast;

	private byte				currentSprite			= 0;
	private byte				animationSpeed;
	// коэф. изменения коорд. в зависимости от направления движения.
	double						xx;
	double						yy;

	/** Счётчики для определения интервала обновления данных. */
	private int					updateImageCounter		= 0;
	private int					updatePosCounter		= 0;
	private int					updateStateCounter		= 0;
	private int					updateStimulusCounter	= 0;

	/** Переменные левого нижнего угла области обзора. */
	public int					xLeft;
	public int					yLeft;

	/** Конструктор. Создает существо типа type (передается константами) в координатах (x, y) */
	public Beast(byte type, int x, int y) {
		this.x = x;
		this.y = y;
		this.beastType = type;
		this.live = true;
		this.target = null;
		this.isRun = false;
		this.isStay = false;
		if (MyWorld.rnd.nextInt(2) == 1)
			this.male = true;
		else
			this.male = false;

		if (type == TYPE_BUNNY) {
			this.spriteRow = 0;
			this.feedType = PLANT_EATING;
			this.MaxHP = 20;
			this.HP = this.MaxHP;
			this.walkSpeed = 3;
			this.runSpeed = 6;
			this.distanceOfSight = 7;
			this.timeOfCorpse = 15;
			this.corpseFoodValue = 40;
			this.size = 32;
			setDirection(0);
			this.animationSpeed = 6;
			this.thinkTime = 100;
			this.timeOfPregnant = 20;

			stimulus[getStimulus("H")] = (double) MyWorld.rnd.nextInt(80) / 100;
			stimulus[getStimulus("E")] = (double) MyWorld.rnd.nextInt(60) / 100;
			stimulus[getStimulus("C")] = 0.5;
			stimulus[getStimulus("A")] = 0.05;
			stimulus[getStimulus("F")] = 0.0;
			stimulus[getStimulus("R")] = (double) MyWorld.rnd.nextInt(60) / 100;

			deltaHunger = 0.03;
			deltaExhaust = 0.03;
			deltaCareful = -0.015;
			deltaAggression = -0.025;
			deltaFear = -0.02;
			deltaReproduction = 0.04;

			this.name = "BUNNY";
			this.image = Art.beasts[0][spriteRow];
		}

		if (type == TYPE_WOLF) {
			this.spriteRow = 0;
			this.feedType = PREDATOR;
			this.MaxHP = 50;
			this.HP = this.MaxHP;
			this.walkSpeed = 4;
			this.runSpeed = 7;
			this.distanceOfSight = 9;
			this.timeOfCorpse = 25;
			this.corpseFoodValue = 100;
			this.size = 64;
			setDirection(0);
			this.animationSpeed = 8;
			this.thinkTime = 100;
			this.timeOfPregnant = 30;

			stimulus[getStimulus("H")] = (double) MyWorld.rnd.nextInt(75) / 100;
			stimulus[getStimulus("E")] = (double) MyWorld.rnd.nextInt(50) / 100;
			stimulus[getStimulus("C")] = 0.5;
			stimulus[getStimulus("A")] = 0.6;
			stimulus[getStimulus("F")] = 0.0;
			stimulus[getStimulus("R")] = (double) MyWorld.rnd.nextInt(50) / 100;

			deltaHunger = 0.04;
			deltaExhaust = 0.02;
			deltaCareful = -0.03;
			deltaAggression = -0.025;
			deltaFear = -0.05;
			deltaReproduction = 0.03;

			this.name = "WOLF";
			this.image = Art.beasts64[0][spriteRow];
		}

	}

	/** Изменяет какой либо основной параметр. Имя на вход - первая буква параметра: H, E, C, A, F или R. */
	public void changeStimulus(String name, double value) {
		stimulus[getStimulus(name)] += value;
		for (int i = 0; i < stimulusType.length; i++) {
			if (stimulus[i] < 0)
				stimulus[i] = 0;
			if (stimulus[i] > 1)
				stimulus[i] = 1;
		}
	}

	/** Возвращает индекс массива параметра, у которого имя начинается на опр. букву */
	public static int getStimulus(String name) {
		if (name.length() == 1) {
			if (name.equalsIgnoreCase("H"))
				return 0;
			if (name.equalsIgnoreCase("E"))
				return 1;
			if (name.equalsIgnoreCase("C"))
				return 2;
			if (name.equalsIgnoreCase("A"))
				return 3;
			if (name.equalsIgnoreCase("F"))
				return 4;
			if (name.equalsIgnoreCase("R"))
				return 5;
		}
		else {
			for (int i = 0; i < stimulusType.length; i++) {
				if (stimulusType[i].toString().equalsIgnoreCase(name))
					return i;
			}
		}

		System.out.println("Error: Method getStimulus return a wrong value.");
		return -1;
	}

	@Override
	public void showStatus() {
		System.out.println("Name: " + name);
		System.out.println("HP: " + HP);
		System.out.println("DIrection: " + direction);
		System.out.println("walkSpeed: " + walkSpeed);
		System.out.println("Male?: " + male);
		for (int i = 0; i < stimulus.length; i++) {
			System.out.println(stimulusType[i] + ": " + stimulus[i]);
		}
		System.out.println("State: " + state);
		System.out.println("StateStep: " + stateStep);
		System.out.println("isPregnant?: " + isPregnant + " " + pregnantTimeCounter);
		System.out.println("Hungry counter : " + hungryDeathCounter);
		System.out.println();
	}

	/** Проверяет условия и ставит нужное изображение Вся логика по анимации - здесь. */
	public void updateImage() {
		updateImageCounter++;
		if (updateImageCounter >= animationSpeed) {
			updateImageCounter %= animationSpeed;

			if (state == STATE_SLEEP || state == STATE_DEAD) {
				if (beastType == TYPE_BUNNY)
					image = Art.beasts[6][spriteRow];
				if (beastType == TYPE_WOLF)
					image = Art.beasts64[6][spriteRow];
				return;
			}

			if (!isStay) {
				currentSprite++;
				currentSprite %= AMOUNT_OF_SPRITE;
			}
			if (beastType == TYPE_BUNNY)
				image = Art.beastsRotated[currentSprite + (AMOUNT_OF_SPRITE * (((direction + Art.ROTATE_DEGREE / 2) % 360) / Art.ROTATE_DEGREE))][spriteRow];

			if (beastType == TYPE_WOLF)
				image = Art.beasts64Rotated[currentSprite + (AMOUNT_OF_SPRITE * (((direction + Art.ROTATE_DEGREE / 2) % 360) / Art.ROTATE_DEGREE))][spriteRow];
		}
	}

	/** Вычисляет следующую позицию существа Вся логика по передвижению - здесь. */
	public void updatePos() {
		updatePosCounter++;
		if (updatePosCounter >= animationSpeed) {
			updatePosCounter %= animationSpeed;

			if (!isStay) {

				if (!isRun) {
					x += (int) (xx * walkSpeed);
					y += (int) (yy * walkSpeed);
				}
				else {
					x += (int) (xx * runSpeed);
					y += (int) (yy * runSpeed);
				}
			}
			xLeft = (int) (x + (distanceOfSight * 32) * Math.cos(Math.toRadians(direction - 180))) + 16;
			yLeft = (int) (y + (distanceOfSight * 32 * Math.sin(Math.toRadians(direction - 180)))) + 16;
		}
	}

	/** Изменяет параметры состояния с течением времени. */
	public void updateStimulus() {
		updateStimulusCounter++;
		if (updateStimulusCounter >= Level.GAME_SPEED * Level.hourTime) {
			updateStimulusCounter %= Level.GAME_SPEED * Level.hourTime;

			if (state != STATE_DEAD) {
				if (state != STATE_EAT) {
					if (state != STATE_SLEEP)
						changeStimulus("H", deltaHunger);
					if (state == STATE_SLEEP)
						changeStimulus("H", deltaHunger / 2);

				}
				if (state != STATE_SLEEP)
					if (!isRun)
						changeStimulus("E", deltaExhaust);
					else
						changeStimulus("E", deltaExhaust * 5);

				changeStimulus("C", deltaCareful);
				changeStimulus("A", deltaAggression);
				changeStimulus("F", deltaFear);

				if (state != STATE_REPRODUCTION)
					changeStimulus("R", deltaReproduction);
			}
		}
	}

	/** Проверяет текущие желания животного и ставит соотв. состояние животного. */
	public void checkStimulus() {
		if (state == STATE_WALKING) {
			if (stimulus[getStimulus("H")] >= 0.7) {
				changeState(STATE_FINDING_FOOD);
				return;
			}
			if (stimulus[getStimulus("E")] >= 0.8) {
				changeState(STATE_SLEEP);
				return;
			}
			if (stimulus[getStimulus("R")] >= 0.75) {
				changeState(STATE_FINDING_REPROD);
				return;
			}
		}
	}

	/** Вычисляет какое состояние сейчас у существа и делает действия в соотв. с ними.. Вся логика по поведению - здесь. */
	public void updateState() {
		updateStateCounter++;
		if (updateStateCounter >= thinkTime) {
			updateStateCounter %= thinkTime;

			stateStep++;

			if (HP <= 0) {
				HP = 0;
				changeState(STATE_DEAD);
			}

			// Обработка смерти от голода
			if (stimulus[getStimulus("H")] > 0.95) {
				hungryDeathCounter++;

				if (hungryDeathCounter >= 40) {
					this.HP -= 1;
					if (this.HP < 0)
						this.HP = 0;
					changeStimulus("F", 0.15);
				}
			}
			else {
				hungryDeathCounter = 0;
			}

			if (stateStep >= 40 && state != STATE_DEAD) {
				changeState(STATE_WALKING);
			}

			// Проверяем наличие цели. Не удалилась ли она.
			if (target != null) {
				if (!Level.isExist(target.getName(), target.x, target.y)) {
					target = null;
				}
			}

			// Проверяем беременность:
			if (isPregnant && live) {
				pregnantTimeCounter++;
				if (pregnantTimeCounter > timeOfPregnant) {
					Level.beasts.add(new Beast(beastType, x, y));
					Level.effects.add(new Effect(Effect.EFFECT_BORN, x, y, 24));
					isPregnant = false;
					pregnantTimeCounter = 0;
				}
			}

			if (state == STATE_WALKING) {
				if (stimulus[getStimulus("F")] >= 0.6 && stimulus[getStimulus("E")] <= 0.85)
					isRun = true;
				else
					isRun = false;

				walkAround();
				checkStimulus();
				return;
			}

			if (state == STATE_RUN_OUT) {
				if (stimulus[getStimulus("E")] <= 0.75)
					isRun = true;
				else
					isRun = false;
				isStay = false;
				walkForward();
				if (stimulus[getStimulus("F")] < 0.1) {
					changeState(STATE_WALKING);
				}
				return;
			}

			if (state == STATE_FINDING_FOOD) {
				if (stimulus[getStimulus("F")] >= 0.6 && stimulus[getStimulus("E")] <= 0.85)
					isRun = true;
				else
					isRun = false;

				if (findFood()) {
					changeState(STATE_EAT);
				}
				else {
					if (stateStep <= 3)
						walkAround();
					else
						walkForward();
				}
				return;
			}

			if (state == STATE_EAT) {
				if (feedType == PREDATOR)
					if (stimulus[getStimulus("E")] <= 0.75)
						isRun = true;
					else
						isRun = false;
				eat();
				return;
			}

			if (state == STATE_FINDING_REPROD) {
				if (findRepr()) {
					changeState(STATE_REPRODUCTION);
				}
				else {
					if (stateStep <= 3)
						walkAround();
					else
						walkForward();
				}
				return;
			}

			if (state == STATE_REPRODUCTION) {
				isRun = false;
				reproduction();
				return;
			}

			if (state == STATE_SLEEP) {
				sleep();
				return;
			}

			if (state == STATE_DEAD) {
				isStay = true;
				timeOfCorpse--;
				Level.effects.add(new Effect(Effect.EFFECT_BLOOD, x, y, 50));
				if (corpseFoodValue <= 0 || timeOfCorpse <= 0) {
					Level.deleteEntity(getName(), x, y);
				}
			}
		}
	}

	/** Вызывает все методы обновления. Вызывать на каждом шаге игры. */
	public void update() {
		updateImage();
		updatePos();
		updateState();
		updateStimulus();
	}

	/** Устанавливает направление животному в абсолютных величинах. */
	public void setDirection(int dir) {
		this.direction = dir % 360;
		this.xx = Math.sin(Math.toRadians(direction));
		this.yy = -Math.cos(Math.toRadians(direction));
		checkPosition();
	}

	/** Меняет направление животному относительно текущего на dir градусов. */
	public void changeDirection(int dir) {
		this.direction += (dir + 360);
		this.direction %= 360;
		this.xx = Math.sin(Math.toRadians(direction));
		this.yy = -Math.cos(Math.toRadians(direction));
		checkPosition();
	}

	/*
	 * Методы ИИ
	 */

	/** Возвращает угол между передаваемым вектором и осью oy. */
	public static int getAngle(int x1, int y1, int x2, int y2) {
		int vx = x2 - x1;
		int vy = y2 - y1;
		if (vx > 0)
			return (int) Math.toDegrees(Math.acos(-vy / Math.sqrt(vx * vx + vy * vy)));
		else
			return (360 - (int) Math.toDegrees(Math.acos(-vy / Math.sqrt(vx * vx + vy * vy))));
	}

	/** Заставляет существо двигаться к своей цели */
	private void moveTo(int x, int y) {
		if (isStay)
			isStay = !isStay;
		setDirection(getAngle(this.x, this.y, x, y));
	}

	/** Заставляет существо двигаться от своей цели */
	@SuppressWarnings("unused")
	private void moveFrom(int x, int y) {
		if (isStay)
			isStay = !isStay;
		setDirection(getAngle(this.x, this.y, x, y) + 180);
	}

	/** Заствляет существо гулять по миру без цели */
	private void walkAround() {
		if (isStay)
			isStay = !isStay;
		setDirection(MyWorld.rnd.nextInt(360));
	}

	/** Заставляет выбрать направление движения от -60 до +60 градусов от текущего направления */
	private void walkForward() {
		if (isStay)
			isStay = !isStay;
		changeDirection(MyWorld.rnd.nextInt(121) - 60);
	}

	/** Проверяет направление движения. Если существо движется за пределы экрана, возвращает ложь. */
	@SuppressWarnings("unused")
	private boolean checkDirection() {
		return true;
	}

	/** Проверяет позицию существа. Если он за далеко за экраном - он перемещается в центр карты. */
	private void checkPosition() {
		if (x < -32 * 8 || x > Level.MAP_WIDTH + 32 * 8 || y < -32 * 8 || y > Level.MAP_HEIGHT + 32 * 8) {
			this.x = Level.MAP_WIDTH / 2;
			this.y = Level.MAP_HEIGHT / 2;
			System.out.println("Bunny was teleported");
		}
	}

	/** Устанавливает целью существа возможный источник еды, находящийся в поле его видимости. Возвращает истину если нашел еду. */
	public boolean findFood() {
		// Немного магии
		target = null;
		targetBeast = null;
		int xStep = (int) (32 * Math.cos(Math.toRadians(direction)));
		int yStep = (int) (32 * Math.sin(Math.toRadians(direction)));
		for (int n = 0; n < distanceOfSight; n++) {
			int xCur = xLeft + xStep * (n) + yStep * (n + 1);
			int yCur = yLeft - xStep * (n + 1) + yStep * (n);
			for (int m = 0; m < (distanceOfSight * 2 - n * 2); m++) {
				xCur += xStep;
				yCur += yStep;

				if (feedType == PLANT_EATING) {
					Entity curEntity = Level.objectOnPoint(xCur, yCur);
					if (curEntity != null) {
						if (curEntity.name.equals("BUSH") || curEntity.name.equals("PLANT")) {
							target = curEntity;
							return true;
						}
					}
				}

				if (feedType == PREDATOR) {
					Beast curBeast = Level.beastOnPoint(xCur, yCur);
					if (curBeast != null) {
						if (curBeast.feedType == PLANT_EATING) {
							target = curBeast;
							targetBeast = curBeast;
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/** Ищет другое животное для размножения. Если нашел возвращает истину. */
	public boolean findRepr() {
		targetBeast = null;
		int xStep = (int) (32 * Math.cos(Math.toRadians(direction)));
		int yStep = (int) (32 * Math.sin(Math.toRadians(direction)));
		for (int n = 0; n < distanceOfSight; n++) {
			int xCur = xLeft + xStep * (n) + yStep * (n + 1);
			int yCur = yLeft - xStep * (n + 1) + yStep * (n);
			for (int m = 0; m < (distanceOfSight * 2 - n * 2); m++) {
				xCur += xStep;
				yCur += yStep;
				Beast curBeast = Level.beastOnPoint(xCur, yCur);
				if (curBeast != null) {
					if (curBeast.male == !this.male && curBeast.beastType == beastType && (curBeast.state == STATE_WALKING || curBeast.state == STATE_FINDING_REPROD)
							&& curBeast.live) {
						targetBeast = curBeast;
						targetBeast.setRepr(x, y, getName());
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Приступить к потреблению пищи выбранной цели. Если цель далеко - он идёт до неё, иначе приступает к еде. */
	private void eat() {
		if (feedType == PLANT_EATING) {
			if (target != null) {
				if (distanceToTarget() > this.size) {
					moveTo(target.x, target.y);
				}
				else {
					moveTo(target.x, target.y);
					isStay = true;
					if (stimulus[getStimulus("H")] >= 0.1) {
						changeStimulus("H", -0.2);
						target.action(-1);
						this.HP += 1;
						if (this.HP >= this.MaxHP)
							this.HP = this.MaxHP;
					}
					else {
						target = null;
						changeState(STATE_WALKING);
					}
				}
			}
			// Если цели нет, а кушать всё еще хочется ^^ или пусть походит
			else {
				if (stimulus[getStimulus("H")] >= 0.1) {
					changeState(STATE_FINDING_FOOD);
				}
				else {
					changeState(STATE_WALKING);
				}
			}
		}

		if (feedType == PREDATOR) {
			if (targetBeast != null) {
				if (distanceTo(targetBeast.x, targetBeast.y) > targetBeast.size) {
					if (distanceTo(targetBeast.x, targetBeast.y) < distanceOfSight * 32)
						moveTo(targetBeast.x, targetBeast.y);
					else
						changeState(STATE_FINDING_FOOD);
				}
				else if (targetBeast.live) {
					moveTo(targetBeast.x, targetBeast.y);
					isStay = true;
					targetBeast.action(10);
				}
				else if (!targetBeast.live) {
					moveTo(targetBeast.x, targetBeast.y);
					isStay = true;
					if (stimulus[getStimulus("H")] >= 0.1) {
						if (targetBeast.corpseFoodValue > 0) {
							changeStimulus("H", -0.1);
							targetBeast.corpseFoodValue -= 5;
							this.HP += 1;
							if (this.HP >= this.MaxHP)
								this.HP = this.MaxHP;
						}
						else {
							changeState(STATE_FINDING_FOOD);
						}
					}
					else {
						target = null;
						targetBeast = null;
						changeState(STATE_WALKING);
					}
				}
			}
		}
	}

	/** Метод размножения. Если цель далеко - идёт до неё. */
	private void reproduction() {
		if (targetBeast != null) {
			// Если цель мертва, необходимо перестать и убегать ;[
			if (!targetBeast.live) {
				changeState(STATE_WALKING);
			}

			if (distanceTo(targetBeast.x, targetBeast.y) > this.size) {
				moveTo(targetBeast.x, targetBeast.y);
			}
			else {
				moveTo(targetBeast.x, targetBeast.y);
				isStay = true;

				if (stimulus[getStimulus("R")] > 0.1 || targetBeast.stimulus[getStimulus("R")] > 0.1) {
					changeStimulus("R", -0.1);

					// Отрисовка сердечка
					if (male) {
						if (!Level.isExist("EFFECT", (x + targetBeast.x + size) / 2 - 16, (y + targetBeast.y + size) / 2 - 16))
							Level.effects.add(new Effect(Effect.EFFECT_HEART, (x + targetBeast.x + size) / 2 - 16, (y + targetBeast.y + size) / 2 - 16, 4));
					}

					if (!Level.isExist(getName(), targetBeast.x, targetBeast.y)) {
						changeState(STATE_WALKING);
						targetBeast = null;
					}
				}
				else {
					if (!male)
						isPregnant = true;
					targetBeast = null;
					changeState(STATE_WALKING);
				}
			}
		}
	}

	/** Сообщает выбранному животному о намерении размножаться */
	public void setRepr(int x, int y, String name) {
		if (state == STATE_WALKING || state == STATE_FINDING_REPROD) {
			targetBeast = Level.beastOnPointAbs(x, y, name);
			changeState(STATE_REPRODUCTION);
		}
	}

	/** заставляет заснуть животное. */
	private void sleep() {
		if (stimulus[getStimulus("E")] >= 0.2) {
			if (!isStay)
				isStay = true;
			changeStimulus("E", -0.1);
			Level.effects.add(new Effect(Effect.EFFECT_SLEEP, x + size / 2, y - 16, 3));
		}
		else
			changeState(STATE_WALKING);
	}

	/** Сменяет состояние животного на другое */
	public void changeState(byte newState) {
		this.state = newState;
		this.stateStep = 0;

		if (state == STATE_EAT || state == STATE_REPRODUCTION || state == STATE_RUN_OUT)
			thinkTime = 50;
		else if (state == STATE_DEAD)
			thinkTime = 200;
		else
			thinkTime = 100;

		if (state == STATE_WALKING) {
			target = null;
		}

		if (state == STATE_FINDING_FOOD) {
			if (stimulus[getStimulus("H")] < 0.1) {
				changeState(STATE_WALKING);
				target = null;
			}
		}

		if (state == STATE_SLEEP) {
			isStay = true;
		}

		if (state == STATE_DEAD) {
			isStay = true;
			live = false;
			target = null;
			targetBeast = null;
		}

		if (state == STATE_REPRODUCTION) {
			isRun = false;
		}
		else {
			isRun = false;
		}
	}

	private int distanceTo(int x, int y) {
		return (int) Math.sqrt((this.x - x) * (this.x - x) + (this.y - y) * (this.y - y));
	}

	private int distanceToTarget() {
		return (int) Math.sqrt((target.x - x) * (target.x - x) + (target.y - y) * (target.y - y));
	}

	@Override
	public boolean action(int value) {
		this.HP -= value;

		Level.effects.add(new Effect(Effect.EFFECT_BLOOD, x, y, 50));

		if (beastType == TYPE_BUNNY) {
			changeStimulus("F", +0.6);
			changeState(STATE_RUN_OUT);
		}

		return true;
	}
}