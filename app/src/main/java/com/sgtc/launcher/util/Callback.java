package com.sgtc.launcher.util;


import java.util.List;

public interface Callback<T> {
    void onDataLoaded(List<T> data);
}
