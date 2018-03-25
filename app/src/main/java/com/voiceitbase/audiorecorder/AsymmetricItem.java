package com.voiceitbase.audiorecorder;

/**
 * Created by kshravi on 24/03/2018 AD.
 */

import android.os.Parcelable;

public interface AsymmetricItem extends Parcelable {
    int getColumnSpan();
    int getRowSpan();
}
