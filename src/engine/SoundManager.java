package engine;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SoundManager {

    private static SoundManager instance;
    private static Logger logger;
    private Clip bgmClip;
    private FloatControl bgmVolumeControl;
    private float backgroundMusicVolume;
    private float soundEffectsVolume;

    private SoundManager() {
        logger = Core.getLogger(); // Core 클래스가 있다고 가정합니다.
        backgroundMusicVolume = 1.0f;
        soundEffectsVolume = 1.0f;
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic(String filepath) {
        try {
            if (bgmClip != null && bgmClip.isOpen()) {
                bgmClip.stop();
                bgmClip.close();
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioInputStream);

            // 지원되는 볼륨 컨트롤 찾기
            bgmVolumeControl = getSupportedVolumeControl(bgmClip);
            if (bgmVolumeControl != null) {
                setVolume(bgmVolumeControl, backgroundMusicVolume);
            } else {
                logger.warning("No supported volume control found for background music.");
            }

            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 배경음악을 반복 재생합니다.
            bgmClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("배경음악 재생 실패: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
        }
    }

    public void resumeBackgroundMusic() {
        if (bgmClip != null && !bgmClip.isRunning()) {
            bgmClip.start();
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void setBackgroundMusicVolume(float gain) {
        if (gain < 0.0f) gain = 0.0f;
        if (gain > 1.0f) gain = 1.0f;
        backgroundMusicVolume = gain;
        if (bgmVolumeControl != null) {
            setVolume(bgmVolumeControl, gain);
        }
    }

    public void playSoundEffect(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
            Clip sfxClip = AudioSystem.getClip();
            sfxClip.open(audioInputStream);

            // 지원되는 볼륨 컨트롤 찾기
            FloatControl sfxVolumeControl = getSupportedVolumeControl(sfxClip);
            if (sfxVolumeControl != null) {
                setVolume(sfxVolumeControl, soundEffectsVolume);
            } else {
                logger.warning("No supported volume control found for sound effect.");
            }

            sfxClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    sfxClip.close();
                }
            });
            sfxClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.warning("효과음 재생 실패: " + e.getMessage());
        }
    }

    public void setSoundEffectsVolume(float gain) {
        if (gain < 0.0f) gain = 0.0f;
        if (gain > 1.0f) gain = 1.0f;
        soundEffectsVolume = gain;
        // 현재 재생 중인 효과음에는 적용되지 않음. 필요 시 추가 구현 가능
    }

    private void setVolume(FloatControl volumeControl, float gain) {
        float min = volumeControl.getMinimum(); // 예: -80.0f
        float max = volumeControl.getMaximum(); // 예: 6.0f
        float dB;
        if (gain <= 0.0f) {
            dB = min;
        } else {
            dB = (float) (Math.log10(gain) * 20.0);
            if (dB < min) dB = min;
            if (dB > max) dB = max;
        }
        volumeControl.setValue(dB);
    }

    /**
     * Clip이 지원하는 볼륨 컨트롤을 찾습니다.
     *
     * @param clip 오디오 Clip
     * @return 지원되는 FloatControl 객체 또는 null
     */
    private FloatControl getSupportedVolumeControl(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
            return (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
        } else {
            // 다른 볼륨 관련 컨트롤을 추가로 확인할 수 있습니다.
            // 예: FloatControl.Type.AUX_GAIN 등
            return null;
        }
    }
}