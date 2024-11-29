// src/entity/ExperiencePool.java
package kr.ac.hanyang.entity;

import java.util.HashSet;
import java.util.Set;
import kr.ac.hanyang.entity.ship.Ship;

/**
 * Implements a pool of recyclable experience points.
 */
public final class ExperiencePool {

    /** Set of already created experience points. */
    private static Set<Experience> pool = new HashSet<>();

    /**
     * Constructor, not called.
     */
    private ExperiencePool() {
    }

    /**
     * Returns an experience point from the pool if one is available, a new one if there isn't.
     *
     * @param positionX Requested position of the experience point in the X axis.
     * @param positionY Requested position of the experience point in the Y axis.
     * @param value     Requested value of the experience point.
     * @return Requested experience point.
     */
    public static Experience getExperience(final int positionX, final int positionY,
        final int value) {
        Experience experience;
        if (!pool.isEmpty()) {
            experience = pool.iterator().next();
            pool.remove(experience);
            experience.setPositionX(positionX);
            experience.setPositionY(positionY);
            experience.setValue(value);
        } else {
            experience = new Experience(positionX, positionY, value);
        }
        return experience;
    }

    /**
     * Adds one or more experience points to the list of available ones.
     *
     * @param experiences Experience points to recycle.
     */
    public static void recycle(final Set<Experience> experiences) {
        pool.addAll(experiences);
    }

    /**
     * Updates all experience points in the pool.
     */
    public static void update(final Set<Experience> experiences) {
        for (Experience experience : experiences) {
            experience.update(); // 경험치의 애니메이션 상태 업데이트
        }
    }

    public static void move(final Set<Experience> experiences, final Ship ship) {
        double movement_X;
        double movement_Y;
        int deltaX;
        int deltaY;
        double distance;

        for (Experience exp : experiences) {
            exp.update();
            // X거리와 Y거리 측정
            deltaX = ship.getPositionX() - exp.getPositionX();
            deltaY = ship.getPositionY() - exp.getPositionY();
            // 플레이어와의 거리 계산
            distance = Math.hypot(deltaX, deltaY);
            // 거리가 0이 아닐때만 플레이어를 향해 이동
            if (distance != 0.0) {
                // X축과 Y축의 거리에 따른 비율을 이용하여 이동량 설정
                movement_X = 10.0 * (deltaX / distance);
                movement_Y = 10.0 * (deltaY / distance);
                exp.move(movement_X, movement_Y);
            }
        }
    }
}