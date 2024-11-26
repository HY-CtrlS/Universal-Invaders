package kr.ac.hanyang.engine;

/**
 * Imposes a cooldown period between two actions.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class Cooldown {

    /** Cooldown duration. */
    private int milliseconds;
    /** Maximum difference between durations. */
    private int variance;
    /** Duration of this run, varies between runs if variance > 0. */
    private int duration;
    /** Beginning time. */
    private long time;
    /** Remaining time when paused. */
    private int remainingTime;
    /** Pause state. */
    private boolean isPaused;

    /**
     * Constructor, established the time until the action can be performed again.
     *
     * @param milliseconds Time until cooldown period is finished.
     */
    protected Cooldown(final int milliseconds) {
        this.milliseconds = milliseconds;
        this.variance = 0;
        this.duration = milliseconds;
        this.time = 0;
        this.remainingTime = 0;
        this.isPaused = false;
    }

    /**
     * Constructor, established the time until the action can be performed again, with a variation
     * of +/- variance.
     *
     * @param milliseconds Time until cooldown period is finished.
     * @param variance     Variance in the cooldown period.
     */
    protected Cooldown(final int milliseconds, final int variance) {
        this.milliseconds = milliseconds;
        this.variance = variance;
        this.time = 0;
        this.remainingTime = 0;
        this.isPaused = false;
    }

    /**
     * Checks if the cooldown is finished.
     *
     * @return Cooldown state.
     */
    public final boolean checkFinished() {
        if (isPaused) {
            return false;
        }
        if ((this.time == 0)
            || this.time + this.duration < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    /**
     * Restarts the cooldown.
     */
    public final void reset() {
        this.time = System.currentTimeMillis();
        this.isPaused = false;
        if (this.variance != 0) {
            this.duration = (this.milliseconds - this.variance)
                + (int) (Math.random()
                * (this.variance + this.variance));
        }
    }

    /**
     * Pauses the cooldown, saving the remaining time.
     */
    public final void pause() {
        if (!isPaused) {
            long currentTime = System.currentTimeMillis();
            this.remainingTime = (int) ((this.time + this.duration) - currentTime);
            this.isPaused = true;
        }
    }

    /**
     * Resumes the cooldown from the paused state.
     */
    public final void resume() {
        if (isPaused) {
            this.time = System.currentTimeMillis();
            this.duration = this.remainingTime;
            this.isPaused = false;
        }
    }

    /**
     * 쿨다운이 멈춰있는지 체크.
     *
     * @return 쿨다운 일시정지 여부.
     */
    public final boolean isPaused() {
        return isPaused;
    }
}
