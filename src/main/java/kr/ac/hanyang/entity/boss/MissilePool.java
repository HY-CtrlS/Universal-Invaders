package kr.ac.hanyang.entity.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.entity.ship.Ship;

public class MissilePool {

    private Set<Missile> missiles;
    private DrawManager drawManager;
    private Logger logger;
    private Ship ship;

    public MissilePool(Ship ship) {
        this.missiles = new HashSet<>();
        this.drawManager = Core.getDrawManager();
        this.logger = Core.getLogger();
        this.ship = ship;
    }

    /**
     * 미사일 생성.
     *
     * @param positionX 미사일 시작 X 좌표.
     * @param positionY 미사일 시작 Y 좌표.
     */
    public void createMissile(final int positionX, final int positionY) {
        Missile missile = new Missile(positionX, positionY, this.ship);
        missiles.add(missile);
        this.logger.info("Missile Created at (" + positionX + ", " + positionY + ").");
    }

    /**
     * 미사일 상태 업데이트.
     */
    public void update() {
        cleanUp();

        for (Missile missile : missiles) {
            if (!missile.isDestroyed()) {
                missile.update();
            }
        }
    }

    /**
     * 미사일 그리기.
     */
    public void draw() {
        for (Missile missile : missiles) {
            if (!missile.isDestroyed()) {
                if (missile.hasExploded()) {
                    drawManager.drawExplosionRadius(missile);
                } else {
                    drawManager.drawEntity(missile, missile.getPositionX(), missile.getPositionY());
                }
            }
        }
    }

    /**
     * 파괴된 미사일 제거.
     */
    private void cleanUp() {
        Set<Missile> toRemove = new HashSet<>();

        for (Missile missile : missiles) {
            if (missile.isDestroyed()) {
                toRemove.add(missile);
            }
        }

        missiles.removeAll(toRemove);
    }

    public Set<Missile> getMissiles() {
        return this.missiles;
    }
}