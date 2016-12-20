package com.seekersoftvendingapp.track;

import android.content.Context;

import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.TakeoutRecord;

/**
 * 提交消费记录信息
 * Created by kjh08490 on 2016/12/20.
 */

public class Track {
    private static Track sTrack;
    private NTrack mNTrack;

    private Track(Context context) {
        this.mNTrack = new NTrack(context.getApplicationContext());
    }

    public static Track getInstance(Context context) {
        if (sTrack == null) {
            sTrack = new Track(context);
        }
        return sTrack;
    }

    /**
     * 提交消费记录
     *
     * @param takeOutRecord
     */
    public void setTakeOutRecordCommand(TakeoutRecord takeOutRecord) {
        this.mNTrack.setTakeOutRecordCommand(takeOutRecord);
    }

    /**
     * 提交借还消费记录
     *
     * @param borrowRecord
     */
    public void setBorrowReturnRecordCommand(BorrowRecord borrowRecord) {
        this.mNTrack.setBorrrowReturnRecordCommand(borrowRecord);
    }

}
