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


    public AsteroidPool() {
        this.asteroids = new HashSet<>();
        this.drawManager = Core.getDrawManager();

        createAsteroid();
    }

    public void draw() {
        for (Asteroid asteroid : asteroids) {
            drawManager.drawEntity(asteroid, asteroid.getPositionX(), asteroid.getPositionY());
        }
    }

    public void createAsteroid() {
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT};
        int positionX = 138;
        int positionY = 303;
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 17; j++) {
                Direction direction = directions[random.nextInt(directions.length)];
                asteroids.add(new Asteroid(positionX, positionY, direction));
                positionX += 26;
            }
            positionX = 138;
            positionY = 589;
        }

        positionY = 303 + 26;
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 11; j++) {
                Direction direction = directions[random.nextInt(directions.length)];
                asteroids.add(new Asteroid(positionX, positionY, direction));
                positionY += 26;
            }
            positionX = 138 + 416;
            positionY = 303 + 26;
        }
    }

    public Set<Asteroid> getAsteroids() {
        return this.asteroids;
    }
}
