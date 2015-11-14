package com.example.owner.swingringer;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;

/**
 * Created by owner on 2015/11/14.
 */
public class SoundRinger {
    private SoundPool mSoundPool;
    private ArrayList<Integer> mSoundIDList;
    private int mSoundIndex;

    SoundRinger(final int soundNum, final int[] soundList, Context context) {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundIDList = new ArrayList<Integer>();
        // index=0は無音とする
        mSoundIDList.add(0);
        for (int i = 0; i < soundNum; i++) {
            mSoundIDList.add(mSoundPool.load(context.getApplicationContext(), soundList[i], 0));
        }
        mSoundIndex = 1;
    }

    public void ring() {
        // インデックス0なら無音
        if (mSoundIndex != 0) {
            mSoundPool.play(mSoundIDList.get(mSoundIndex), 1.0F, 1.0F, 0, 0, 1.0F);
        }
    }


    public void release() {
        mSoundPool.release();
    }

    protected int switchSound() {
        if ( mSoundIndex + 1 > mSoundIDList.size() - 1) {
            mSoundIndex = 0;
        } else {
            mSoundIndex++;
        }
        return mSoundIndex;
    }
}
