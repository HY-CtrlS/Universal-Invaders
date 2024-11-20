// src/entity/ExperiencePool.java
package entity;

import java.util.HashSet;
import java.util.Set;

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
    public static Experience getExperience(final int positionX, final int positionY, final int value) {
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
}