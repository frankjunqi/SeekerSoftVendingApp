package com.seekersoftvendingapp.track;

import android.content.Context;

import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.BorrowRecordDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.TakeoutRecord;
import com.seekersoftvendingapp.database.table.TakeoutRecordDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/20.
 */

public class EventController {

    private Context mContext;
    // 等待提交的出货记录list
    private List<TakeoutRecord> takeOutRecordList = new ArrayList<>();
    private TakeoutRecordDao takeoutRecordDao;

    // 借还记录接口
    private List<BorrowRecord> borrowRecordList = new ArrayList<>();
    private BorrowRecordDao borrowRecordDao;

    public EventController(Context mContext) {
        this.mContext = mContext;
        DaoSession daoSession = ((SeekersoftApp) mContext.getApplicationContext()).getDaoSession();
        takeoutRecordDao = daoSession.getTakeoutRecordDao();
        borrowRecordDao = daoSession.getBorrowRecordDao();
    }

    // 往数据库中更新这条记录 + 等待提交的出货记录list插入这条数据
    public void addTakeOutRecord(TakeoutRecord takeOutRecord) {
        takeoutRecordDao.insertOrReplaceInTx(takeOutRecord);
        this.takeOutRecordList.add(takeOutRecord);
    }

    public int getTakeOutRecordSize() {
        return this.takeOutRecordList.size();
    }

    public List<TakeoutRecord> getTakeOutRecordList() {
        return takeOutRecordList;
    }

    public void setTaketOutRecordOk() {
        for (TakeoutRecord takeOutRecord : takeOutRecordList) {
            takeOutRecord.setIsDel(false);
        }
        takeoutRecordDao.insertOrReplaceInTx(takeOutRecordList);
        takeOutRecordList.clear();
    }


    // --------------------- borrow return recoid -------------------------
    public void addBorrowReturnRecord(BorrowRecord borrowRecord) {
        borrowRecordDao.insertOrReplaceInTx(borrowRecord);
        borrowRecordList.add(borrowRecord);
    }

    public int getBorrowReturnRecordSize() {
        return borrowRecordList.size();
    }

    public List<BorrowRecord> getBorrowReturnRecordList() {
        return borrowRecordList;
    }

    public void setBorrowReturnRecordOk() {
        for (BorrowRecord borrowRecord : borrowRecordList) {
            borrowRecord.setIsFlag(false);
        }
        borrowRecordDao.insertOrReplaceInTx(borrowRecordList);
        borrowRecordList.clear();
    }

}
