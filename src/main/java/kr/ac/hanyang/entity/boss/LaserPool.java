package kr.ac.hanyang.entity.boss;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
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

    public void createLaser() {
        Laser laser = new Laser(this.ship.getPositionX(), this.ship.getPositionY());
        lasers.add(laser);
        this.logger.info("Laser Attack!");
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
