package com.seekersoftvendingapp.track;

import android.content.Context;

import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.TakeoutRecord;

/**
 * 提交消费记录信息
 * Created by kjh08490 on 2016/12/20.
 */

public class Track {
    private static Track sTrack;
    private TakeOutNTrack mTakeOutNTrack;
    private BorrowReturnNTrack mBorrowReturnNTrack;
    private PassageDao passageDao;

    private Track(Context context) {
        this.mTakeOutNTrack = new TakeOutNTrack(context.getApplicationContext());
        this.mBorrowReturnNTrack = new BorrowReturnNTrack(context.getApplicationContext());
        DaoSession daoSession = ((SeekersoftApp) context.getApplicationContext()).getDaoSession();
        passageDao = daoSession.getPassageDao();
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
    public void setTakeOutRecordCommand(Passage passage, TakeoutRecord takeOutRecord) {
        setTakeOutRecordCommand(passage, takeOutRecord, "");
    }

    /**
     * 提交消费记录
     *
     * @param takeOutRecord
     * @param objectId
     */
    public void setTakeOutRecordCommand(Passage passage, TakeoutRecord takeOutRecord, String objectId) {
        passageDao.insertOrReplaceInTx(passage);
        mTakeOutNTrack.setTakeOutRecord(takeOutRecord, objectId);
    }

    /**
     * 提交借还消费记录
     *
     * @param borrowRecord
     */
    public void setBorrowReturnRecordCommand(Passage passage, BorrowRecord borrowRecord) {
        setBorrowReturnRecordCommand(passage, borrowRecord, "");
    }

    public void setBorrowReturnRecordCommand(Passage passage, BorrowRecord borrowRecord, String objectId) {
        passageDao.insertOrReplaceInTx(passage);
        mBorrowReturnNTrack.setBorrrowReturnRecordCommand(borrowRecord, objectId);
    }
}
