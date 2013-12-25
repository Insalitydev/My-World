package myWorld;

import java.awt.Color;
import java.awt.Graphics;

import myWorld.entity.Beast;

public class HUD {

	// HUD Верхние окошка
	public int				correctHUD		= 0;
	public int				HUDLength		= 6;
	private String			tmpString;
	public static final int	HUD_X			= 24;
	public static final int	HUD_Y			= 20;

	// HUD Beast Info
	public int				correctHUDBeast	= 0;
	public static final int	HUDBeast_X		= 24;
	public static final int	HUDBeast_Y		= 116;
	public static final int	HUDBeastWidth	= 3;
	public static final int	HUDBeastHeight	= 4;

	public void paintHUD(Graphics g) {
		// UI будет рисоваться всегда на экране
		// Отрисовка двух окошек верхних:
		for (int i = 0; i < HUDLength; i++) {
			if (i == 0) {
				g.drawImage(Art.objects[0][2], HUD_X + 32 * i, HUD_Y + correctHUD, Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(), null); // Левый
				g.drawImage(Art.objects[0][2], Level.CAMERA_WIDTH - (HUD_X + 32 * i), HUD_Y + correctHUD, -Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(),
						null); // Правый
			}
			else if (i == HUDLength - 1) {
				g.drawImage(Art.objects[2][2], HUD_X + 32 * i, HUD_Y + correctHUD, Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(), null);
				g.drawImage(Art.objects[2][2], Level.CAMERA_WIDTH - (HUD_X + 32 * i), HUD_Y + correctHUD, -Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(),
						null);
			}
			else {
				g.drawImage(Art.objects[1][2], HUD_X + 32 * i, HUD_Y + correctHUD, Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(), null);
				g.drawImage(Art.objects[1][2], Level.CAMERA_WIDTH - (HUD_X + 32 * i), HUD_Y + correctHUD, -Art.objects[0][2].getWidth(), Art.objects[0][2].getHeight(),
						null);
			}
		}

		// Отрисовка окошка под инфу зверей:
		if (Level.choosenObject != null && ( Level.choosenObject.getName().equals("BUNNY") ||  Level.choosenObject.getName().equals("WOLF"))) {
			for (int i = 0; i < HUDBeastHeight; i++) {
				for (int j = 0; j < HUDBeastWidth; j++) {
					// Отрисовка внешней стороны
					if (j == 0)
						if (i == 0)
							g.drawImage(Art.objects[0][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32), 32, 32, null);
						else if (i == HUDBeastHeight - 1)
							g.drawImage(Art.objects[0][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32) + 32, 32, -32, null);
						else
							g.drawImage(Art.objects[2][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32), 32, 32, null);
					else if (j == HUDBeastWidth - 1)
						if (i == 0)
							g.drawImage(Art.objects[0][3], HUDBeast_X + (j * 32) + 32, HUDBeast_Y + correctHUDBeast + (i * 32), -32, 32, null);
						else if (i == HUDBeastHeight - 1)
							g.drawImage(Art.objects[0][3], HUDBeast_X + (j * 32) + 32, HUDBeast_Y + correctHUDBeast + (i * 32) + 32, -32, -32, null);
						else
							g.drawImage(Art.objects[2][3], HUDBeast_X + (j * 32) + 32, HUDBeast_Y + correctHUDBeast + (i * 32), -32, 32, null);
					else if (i == 0)
						g.drawImage(Art.objects[1][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32), 32, 32, null);
					else if (i == HUDBeastHeight - 1)
						g.drawImage(Art.objects[1][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32) + 32, 32, -32, null);
					// отрисовка внутренней части:
					else
						g.drawImage(Art.objects[3][3], HUDBeast_X + (j * 32), HUDBeast_Y + correctHUDBeast + (i * 32), 32, 32, null);
				}
			}
		}

		// Заполнение HUD'a
		// Левый
		tmpString = String.format("The tile is : %s(%d:%d)", Level.map[(Level.curX + Level.mouseX) / 32][(Level.curY + Level.mouseY) / 32].getName(),
				(Level.curX + Level.mouseX) / 32, (Level.curY + Level.mouseY) / 32);
		g.drawString(tmpString, HUD_X + 8, HUD_Y + 14 + correctHUD);
		tmpString = String.format("Creatures: %d", Level.beasts.size());
		g.drawString(tmpString, HUD_X + 8, HUD_Y + 26 + correctHUD);

		// Правый
		if (Level.choosenObject != null) {
			tmpString = String.format("Choosen: %s (%d, %d)", Level.choosenObject.getName(), Level.choosenObject.x / 32, Level.choosenObject.y / 32);
			g.drawString(tmpString, Level.CAMERA_WIDTH - (HUD_X) - (32 * (HUDLength - 1)), HUD_Y + 14 + correctHUD);
		}
		else {
			tmpString = String.format("Choosen: %s", "Nothing");
			g.drawString(tmpString, Level.CAMERA_WIDTH - (HUD_X) - (32 * (HUDLength - 1)), HUD_Y + 14 + correctHUD);
		}

		tmpString = String.format("Day: %d, time: %dh", Level.days, Level.hours);
		g.drawString(tmpString, Level.CAMERA_WIDTH - (HUD_X) - (32 * (HUDLength - 2)), HUD_Y + 26 + correctHUD);

		// Заполнения инфы животного:
		if (Level.choosenObject != null) {
			if (Level.choosenObject.getName().equals("BUNNY") || Level.choosenObject.getName().equals("WOLF")) {
				g.drawImage(Level.choosenObject.getImage(), HUDBeast_X, HUDBeast_Y, 32, 32, null);
				// Отрисвка полоски здоровья и имени поверх него.
				g.setColor(Color.BLACK);
				g.fillRect(HUDBeast_X + 32, HUDBeast_Y + 10, 56, 12);

				// Полоска здоровья. Если пол мужской- полоска красная. Женский - Синяя.
				if (Level.choosenBeast.male)
					g.setColor(new Color(200, 25, 50, 225));
				else
					g.setColor(new Color(25, 50, 250, 225));
				g.fillRect(HUDBeast_X + 33, HUDBeast_Y + 11, 54 * Level.choosenBeast.HP/Level.choosenBeast.MaxHP, 10);

				g.setColor(Color.WHITE);
				g.drawString(Level.choosenObject.getName(), HUDBeast_X + 4 + 32, HUDBeast_Y + 4 + 16);

				// Название параметров:
				g.drawString("H", HUDBeast_X + 8, HUDBeast_Y + 7 + 32);
				g.drawString("E", HUDBeast_X + 8, HUDBeast_Y + 7 + 48);
				g.drawString("C", HUDBeast_X + 8, HUDBeast_Y + 7 + 64);
				g.drawString("A", HUDBeast_X + 8, HUDBeast_Y + 7 + 80);
				g.drawString("F", HUDBeast_X + 8, HUDBeast_Y + 7 + 96);
				g.drawString("R", HUDBeast_X + 8, HUDBeast_Y + 7 + 112);

				// Фон - черный прямоугольник для каждого.
				g.setColor(Color.BLACK);
				for (int i = 0; i < 6; i++) {
					g.fillRect(HUDBeast_X + 8 + 16, HUDBeast_Y + 28 + (i * 16), 64, 12);
				}

				// Заполнение этих прямоугольников
				g.setColor(new Color(200, 25, 50, 225));
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("H")]), 10);
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29 + 16, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("E")]), 10);
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29 + 32, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("C")]), 10);
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29 + 48, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("A")]), 10);
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29 + 64, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("F")]), 10);
				g.fillRect(HUDBeast_X + 9 + 16, HUDBeast_Y + 29 + 80, (int) (62 * Level.choosenBeast.stimulus[Beast.getStimulus("R")]), 10);
			}
		}
	}
}
