package com.voiceitbase.audiorecorder;

import android.widget.ListAdapter;


import java.util.List;

public interface DemoAdapter extends ListAdapter {

  void appendItems(List<DemoItem> newItems);

  void setItems(List<DemoItem> moreItems);
}
