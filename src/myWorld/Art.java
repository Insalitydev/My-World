package myWorld;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import myWorld.entity.Beast;

/**
 * Класс, содержащий всю графику, и предоставляющий к ней доступ по массивам. Загружает изображения и представляет их в виде массива.
 * 
 * @author Epso
 * @version 1.0.0
 */
public class Art {
	public static AffineTransform	xform			= new AffineTransform();

	public static BufferedImage[][]	tiles			= split(load("/TileSet.png"), 32, 32);
	public static BufferedImage[][]	objects			= split(load("/ObjectSet.png"), 32, 32);
	public static BufferedImage[][]	beasts			= split(load("/BeastSet.png"), 32, 32);
	public static BufferedImage[][]	beastsRotated	= splitRotate();
	/** Изображения зверей с разрешением 64х64 */
	public static BufferedImage[][]	beasts64		= split(load("/Beast64Set.png"), 64, 64);
	public static BufferedImage[][]	beasts64Rotated	= splitRotate64();

	private static BufferedImage	result;
	private static Graphics2D		g;

	/** Каждый сколько градусов делать предзагрузку поворота изображений животных.*/
	public final static int			ROTATE_DEGREE	= 15;

	/** Загружает изображение с диска */
	public static BufferedImage load(String name) {
		try {
			BufferedImage org = ImageIO.read(Art.class.getResource(name));
			BufferedImage res = new BufferedImage(org.getWidth(), org.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = res.getGraphics();
			g.drawImage(org, 0, 0, null, null);
			g.dispose();
			return res;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Изменяет масштаб изображения. */
	@SuppressWarnings("unused")
	private static BufferedImage scale(BufferedImage src, int scale) {
		int w = src.getWidth() * scale;
		int h = src.getHeight() * scale;
		BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = res.getGraphics();
		g.drawImage(src.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING), 0, 0, null);
		g.dispose();
		return res;
	}

	/** Разрезает изображение на изображения с разрешением xs*ys*/
	private static BufferedImage[][] split(BufferedImage src, int xs, int ys) {
		int xSlices = src.getWidth() / xs;
		int ySlices = src.getHeight() / ys;
		BufferedImage[][] res = new BufferedImage[xSlices][ySlices];
		for (int x = 0; x < xSlices; x++) {
			for (int y = 0; y < ySlices; y++) {
				res[x][y] = new BufferedImage(xs, ys, BufferedImage.TYPE_INT_ARGB);
				Graphics g = res[x][y].getGraphics();
				g.drawImage(src, -x * xs, -y * ys, null);
				g.dispose();
			}
		}
		return res;
	}

	/** Делает предзагрузку поворота изображений животных.
	 * Необходио для мгновенного доступа к ним.
	 */
	private static BufferedImage[][] splitRotate() {
		BufferedImage[][] res = new BufferedImage[Beast.AMOUNT_OF_SPRITE * (360 / ROTATE_DEGREE)][10];
		for (int x = 0; x < Beast.AMOUNT_OF_SPRITE * (360 / ROTATE_DEGREE); x++) {
			for (int y = 0; y < 10; y++) {
				res[x][y] = rotate(beasts[x % Beast.AMOUNT_OF_SPRITE][y], (x / Beast.AMOUNT_OF_SPRITE) * ROTATE_DEGREE);

			}
		}
		return res;
	}

	private static BufferedImage[][] splitRotate64() {
		BufferedImage[][] res = new BufferedImage[Beast.AMOUNT_OF_SPRITE * (360 / ROTATE_DEGREE)][10];
		for (int x = 0; x < Beast.AMOUNT_OF_SPRITE * (360 / ROTATE_DEGREE); x++) {
			for (int y = 0; y < 10; y++) {
				res[x][y] = rotate(beasts64[x % Beast.AMOUNT_OF_SPRITE][y], (x / Beast.AMOUNT_OF_SPRITE) * ROTATE_DEGREE);

			}
		}
		return res;
	}

	/** Поворачивает изображение на градус angle*/
	public static BufferedImage rotate(BufferedImage image, double angle) {
		// и тут тоже магия
		angle /= 2;
		result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = result.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		xform.setToIdentity();
		xform.translate(0, 0);
		xform.rotate(Math.toRadians(angle), image.getWidth() / 2, image.getHeight() / 2);
		g.transform(xform);
		g.drawImage(image, xform, null);

		g.dispose();
		return result;
	}
}