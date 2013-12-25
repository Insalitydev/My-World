package myWorld.entity;

import java.util.Random;

import myWorld.Art;

public class Tile extends Entity {
	public static byte			AMOUNT_OF_TILES	= 4;					// кол-во нарисованных тайлов для каждого типа поверхности

	int							type;
	public static final byte	GRASS			= 0;
	public static final byte	DIRT			= 1;
	public static final byte	STONE			= 2;
	int							tileNumber;

	static EnumTiles			tileType[]		= EnumTiles.values();
	static Random				rnd				= new Random();

	/** Типы указаны в enum-классе EnumTiles. 0 - GRASS; 1 - DIRT; 2 - STONE. */
	public Tile(int x, int y, int type) {

		this.x = x;
		this.y = y;
		this.type = type;
		this.name = tileType[type].toString();
		this.tileNumber = rnd.nextInt(4);
		this.image = Art.tiles[tileNumber][type];
	}

	public int getType() {
		return type;
	}

	/** Меняет картинку тайла на следующий такой же тип. */
	public void nextType() {
		type++;
		type = type % (tileType.length);
		image = Art.tiles[tileNumber][type];
		name = tileType[type].toString();
	}

	/** Меняет тип тайла на следующий. */
	public void nextTile() {
		tileNumber++;
		tileNumber = tileNumber % AMOUNT_OF_TILES;
		image = Art.tiles[tileNumber][type];
	}

	@Override
	public void showStatus() {
		// Nothing
	}

	@Override
	public boolean action(int value) {
		return true;
	}
}
