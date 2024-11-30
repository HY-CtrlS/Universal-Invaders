package kr.ac.hanyang.entity.boss;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import kr.ac.hanyang.engine.Core;
import kr.ac.hanyang.engine.DrawManager;
import kr.ac.hanyang.entity.Entity.Direction;
import kr.ac.hanyang.screen.BossScreen;

public final class AsteroidPool {

    private Set<Asteroid> asteroids;
    private DrawManager drawManager;
    Random random = new Random();


    public AsteroidPool(final BossScreen screen) {
        this.asteroids = new HashSet<>();
        this.drawManager = Core.getDrawManager();

        createAsteroid(screen);
    }

    public void draw() {
        for (Asteroid asteroid : asteroids) {
            drawManager.drawEntity(asteroid, asteroid.getPositionX(), asteroid.getPositionY());
        }
    }

    public void createAsteroid(final BossScreen screen) {
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT};
        int positionX = 100;
        int positionY = screen.getHeight() - 532;
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 21; j++) {
                Direction direction = directions[random.nextInt(directions.length)];
                asteroids.add(new Asteroid(positionX, positionY, direction));
                positionX += 25;
            }
            positionX = 100;
            positionY = screen.getHeight() - 168;
        }

        positionY = screen.getHeight() - 506;
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 13; j++) {
                Direction direction = directions[random.nextInt(directions.length)];
                asteroids.add(new Asteroid(positionX, positionY, direction));
                positionY += 26;
            }
            positionX = screen.getWidth() - 120;
            positionY = screen.getHeight() - 506;
        }
    }

    public Set<Asteroid> getAsteroids() {
        return this.asteroids;
    }
}
