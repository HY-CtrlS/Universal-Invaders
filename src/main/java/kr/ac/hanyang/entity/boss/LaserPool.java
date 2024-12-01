package kr.ac.hanyang.entity.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.entity.ship.Ship;

public final class LaserPool {

    private Set<Laser> lasers;
    private DrawManager drawManager;
    private Ship ship;
    private Logger logger;

    public LaserPool(Ship ship) {
        this.lasers = new HashSet<>();
        this.drawManager = Core.getDrawManager();
        this.ship = ship;
        this.logger = Core.getLogger();
    }

    public void update() {
        cleanUp();

        for (Laser laser : lasers) {
            if (!laser.isDone()) {
                laser.update();
            }
        }
    }

    public void draw() {
        for (Laser laser : lasers) {
            if (!laser.isDone()) {
                drawManager.drawEntity(laser, 0, laser.getPositionY());
            }
        }
    }

    // Y좌표에 가로 레이저 생성
    public void createHorizontalLaser(int positionY) {
        Laser laser = new Laser(this.ship.getPositionX(), positionY, Direction.LEFT);
        lasers.add(laser);
        this.logger.info("Create Laser on Y :" + positionY);
    }

    public void cleanUp() {
        Set<Laser> toRemove = new HashSet<>();

        for (Laser laser : lasers) {
            if (laser.isDone()) {
                toRemove.add(laser);
            }
        }
        lasers.removeAll(toRemove);
    }

    public Set<Laser> getLasers() {
        return this.lasers;
    }
}
