package com.ubd_app.Ride.Guide;

import android.media.MediaPlayer;
import com.ubd_app.Main;
import com.ubd_app.R;

import java.util.ArrayList;
import java.util.List;

public class DriveSounds {

    private Main main;

    private List<MediaPlayer> Distance = new ArrayList<>();
    private List<MediaPlayer> RightTurnMedia = new ArrayList<>();
    private List<MediaPlayer> LeftTurnMedia = new ArrayList<>();
    private List<MediaPlayer> CrossWalkMedia = new ArrayList<>();

    private List<MediaPlayer> ETCMedia = new ArrayList<>();

    public DriveSounds(Main main) {
        this.main = main;
        setMediaLoad();
    }

    public void playETCAudio(int ETCIndex) {
        new SoundThread(ETCMedia.get(ETCIndex)).start();
    }

    public void playAudio(int sounds1, int sounds2) {
        if (sounds2 == 12) {
            new SoundThread(Distance.get(sounds1), LeftTurnMedia.get(sounds2));
        } else if (sounds2 == 13) {
            new SoundThread(Distance.get(sounds1),RightTurnMedia.get(sounds2));
        } else if (211< sounds2 && sounds2 <217) {
            //횡단보도
            //왼쪽 sounds2 == 212 || sounds2 == 214 || sounds2 == 215
            //오른쪽 sounds2 == 213 || sounds2 == 216 || sounds2 == 217
            new SoundThread(Distance.get(sounds1),CrossWalkMedia.get(sounds2));
        } else if (sounds2 == 201) {
            new SoundThread(Distance.get(sounds1),ETCMedia.get(3));
        } else {
            new SoundThread(Distance.get(sounds1),ETCMedia.get(1));
        }

    }

    private void setMediaLoad() {
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_left));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_left_front_50m));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_left_front_150m));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_left_front_300m));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.left_crosswalk_front_50m));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.left_crosswalk_front_150m));
        LeftTurnMedia.add(new MediaPlayer().create(main, R.raw.left_crosswalk_front_300m));

        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.ture_right));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_right_front_50m));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_right_front_150m));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.turn_right_front_300m));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.right_crosswalk_front_50m));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.right_crosswalk_front_150m));
        RightTurnMedia.add(new MediaPlayer().create(main, R.raw.right_crosswalk_front_300m));

        CrossWalkMedia.add(new MediaPlayer().create(main, R.raw.crosswalk_next_left));
        CrossWalkMedia.add(new MediaPlayer().create(main, R.raw.crosswalk_next_right));

        ETCMedia.add(new MediaPlayer().create(main, R.raw.start));
        ETCMedia.add(new MediaPlayer().create(main, R.raw.go_straght));
        ETCMedia.add(new MediaPlayer().create(main, R.raw.research));
        ETCMedia.add(new MediaPlayer().create(main, R.raw.arrive));
    }

    public void AllRelease() {
        ReleaseMedia(RightTurnMedia);
        ReleaseMedia(LeftTurnMedia);
        ReleaseMedia(CrossWalkMedia);
        ReleaseMedia(ETCMedia);
    }

    private void ReleaseMedia(List<MediaPlayer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            list.get(i).release();
            list.remove(i);
        }
    }
}
