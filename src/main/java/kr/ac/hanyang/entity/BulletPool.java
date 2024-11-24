package kr.ac.hanyang.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a pool of recyclable bullets.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public final class BulletPool {

    /** Set of already created bullets. */
    private static Set<Bullet> pool = new HashSet<Bullet>();

    /**
     * Constructor, not called.
     */
    private BulletPool() {

    }

    /**
     * Returns a bullet from the pool if one is available, a new one if there isn't.
     *
     * @param positionX Requested position of the bullet in the X axis.
     * @param positionY Requested position of the bullet in the Y axis.
     * @param speed     Requested speed of the bullet.
     * @param damage    설정할 총알의 피해량.
     * @param direction 설정할 총알의 방향.
     * @param classify  설정할 총할의 진영.
     * @return Requested bullet.
     */
    public static Bullet getBullet(final int positionX,
        final int positionY, final int speed, int damage, Entity.Direction direction,
        int classify) {
        Bullet bullet;
        if (!pool.isEmpty()) {
            bullet = pool.iterator().next();
            pool.remove(bullet);
            bullet.setPositionX(positionX - bullet.getWidth() / 2);
            bullet.setPositionY(positionY);
            bullet.setSpeed(speed);
            bullet.setDamage(damage);
            bullet.setSprite();
            bullet.setDirection(direction);
            bullet.setClassify(classify);

        } else {
            bullet = new Bullet(positionX, positionY, speed, damage, direction, classify);
            bullet.setPositionX(positionX - bullet.getWidth() / 2);
        }

        // 만약 함선 ID가 4이면 총알 관통으로 설정, 아니면 관통 해제
        if (classify == 4) {
            bullet.setPiercing(true);
        } else {
            bullet.setPiercing(false);
        }

        return bullet;
    }

    /**
     * Adds one or more bullets to the list of available ones.
     *
     * @param bullet Bullets to recycle.
     */
    public static void recycle(final Set<Bullet> bullet) {
        pool.addAll(bullet);
    }
}