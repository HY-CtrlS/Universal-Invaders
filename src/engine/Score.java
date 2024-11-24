package engine;

/**
 * Implements a high score record.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class Score implements Comparable<Score> {

    /** Player's name. */
    private String name;
    /** Score points. */
    private int score;

    private int survivalTime;

    /**
     * Constructor.
     *
     * @param name  Player name, three letters.
     * @param score Player score.
     */
    public Score(final String name, final int score, final int survivalTime) {
        this.name = name;
        this.score = score;
        this.survivalTime = survivalTime;
    }

    /**
     * Getter for the player's name.
     *
     * @return Name of the player.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Getter for the player's score.
     *
     * @return High score.
     */
    public final int getScore() {
        return this.score;
    }

    /**
     * Getter for the player's survival time.
     *
     * @return Survival time.
     */
    public final int getSurvivalTime(){ return this.survivalTime;}

    /**
     * Orders the scores descending by score.
     *
     * @param score Score to compare the current one with.
     * @return Comparison between the two scores. Positive if the current one is smaller, positive
     * if its bigger, zero if its the same.
     */



    @Override
    public final int compareTo(final Score score) {
        // 1차적으로 score를 비교
        int comparison = this.score < score.getScore() ? 1 : this.score > score.getScore() ? -1 : 0;

        // score가 동일한 경우 survivalTime으로 비교
        if (comparison == 0) {
            comparison = this.survivalTime < score.getSurvivalTime() ? 1
                : this.survivalTime > score.getSurvivalTime() ? -1 : 0;
        }

        return comparison;
    }

}
