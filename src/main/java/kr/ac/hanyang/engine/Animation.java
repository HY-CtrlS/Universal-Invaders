package kr.ac.hanyang.engine;

public class Animation {
    private int width;
    private int height;
    private int numberOfFrames;
    private AnimationFrame[] frames;

    /** 퍼블릭 생성자 */
    public Animation(int width, int height, int numberOfFrames) {
        this.width = width;
        this.height = height;
        this.numberOfFrames = numberOfFrames;
        frames = new AnimationFrame[numberOfFrames];
    }

    public void loadAnimation(byte[] bytes) {
        char c;
        int n = 0;

        for (int i = 0; i < numberOfFrames; i++) {
            AnimationFrame animFrame = new AnimationFrame(i, this.width, this.height);
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    do {
                        c = (char) bytes[n];
                        n++;
                    } while (c != '0' && c != '1');
                    animFrame.getImage()[j][k] = c == '1';
                }
            }
            this.addFrame(animFrame);
        }
    }

    public void addFrame(AnimationFrame frame) {
        this.frames[frame.getIdx()] = frame;
    }

    public AnimationFrame[] getFrames() {
        return frames;
    }
}
