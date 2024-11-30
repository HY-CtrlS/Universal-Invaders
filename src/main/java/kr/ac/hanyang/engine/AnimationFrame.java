package kr.ac.hanyang.engine;

public class AnimationFrame {
    private boolean[][] image;
    private int duration = 1;
    private int idx;

    /** 퍼블릭 생성자 */
    public AnimationFrame(int idx, int width, int height) {
        this.idx = idx;
        this.image = new boolean[width][height];
    }

    public boolean[][] getImage() {
        return this.image;
    }

    public void setImage(boolean[][] image) {
        this.image = image;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIdx() {
        return this.idx;
    }
}
