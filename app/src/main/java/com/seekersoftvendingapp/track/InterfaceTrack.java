package com.seekersoftvendingapp.track;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池共享
 * Created by kjh08490 on 2016/12/21.
 */

public interface InterfaceTrack {

    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
}
