package kr.ac.hanyang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AnimationManagerTest {
    byte[] bytes;

    @BeforeEach
    public void setUp() throws IOException {
        ClassLoader cl = AnimationManager.class.getClassLoader();
        InputStream inputStream = cl.getResourceAsStream(
            "animation/TestAnimation");
        this.bytes = inputStream.readAllBytes();
    }

    @Test
    public void loadAnimationTest() {
        Animation animation = new Animation(3, 3, 2);
        animation.loadAnimation(this.bytes);

        System.out.println(printFrames(animation));
    }

    public String printFrames(Animation anim) {
        String result = "";

        AnimationFrame[] frames = anim.getFrames();
        for (AnimationFrame frame : frames) {
            result = result.concat("Frame " + frame.getIdx() + ":\n");
            boolean[][] image = frame.getImage();
            for (int i = 0; i < image.length; i++) {
                for (int j = 0; j < image[i].length; j++) {
                    String s = image[i][j] ? "1" : "0";
                    result = result.concat(s);
                }
                result = result.concat("\n");
            }
        }
        return result;
    }
}
