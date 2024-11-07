package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {

	/** Time between shots. */
	private static final int SHOOTING_INTERVAL = 750;
	/** Speed of the bullets shot by the ship. */
	private static final int BULLET_SPEED = 6;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;
	/** 함선이 바라보고 있는 뱡향 */
	private static String direction = "UP";
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param direction
	 * 			  함선의 초기 방향.
	 */
	public Ship(final int positionX, final int positionY, String direction) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
		this.direction = direction;
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		this.direction = "RIGHT";
		this.positionX += SPEED;
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		this.direction = "LEFT";
		this.positionX -= SPEED;
	}

	/**
	 * Moves the ship speed units up, or until the top screen border is reached.
	 */
	public final void moveUp() {
		this.direction = "UP";
		this.positionY -= SPEED;
	}

	/**
	 * Moves the ship speed units down, or until the bottom screen border is reached.
	 */
	public final void moveDown() {
		this.direction = "DOWN";
		this.positionY += SPEED;
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets
	 *            List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX + this.width / 2,
					positionY, BULLET_SPEED, direction, "SHIP"));
			return true;
		}
		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else
			this.spriteType = SpriteType.Ship;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return SPEED;
	}

	/**
	 * 함선의 방향을 얻는 Getter
	 *
	 * @return 함선의 방향.
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * Getter for the ship's X position.
	 *
	 * @return The X coordinate of the ship.
	 */
	public int getX() {
		return this.positionX;
	}

	/**
	 * Getter for the ship's Y position.
	 *
	 * @return The Y coordinate of the ship.
	 */
	public int getY() {
		return this.positionY;
	}
}
