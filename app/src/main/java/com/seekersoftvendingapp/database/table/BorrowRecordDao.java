package com.seekersoftvendingapp.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BORROW_RECORD".
*/
public class BorrowRecordDao extends AbstractDao<BorrowRecord, Long> {

    public static final String TABLENAME = "BORROW_RECORD";

    /**
     * Properties of entity BorrowRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property IsFlag = new Property(1, Boolean.class, "isFlag", false, "IS_FLAG");
        public final static Property Passage = new Property(2, String.class, "passage", false, "PASSAGE");
        public final static Property Card = new Property(3, String.class, "card", false, "CARD");
        public final static Property Borrow = new Property(4, Boolean.class, "borrow", false, "BORROW");
        public final static Property Result = new Property(5, Boolean.class, "result", false, "RESULT");
        public final static Property Time = new Property(6, java.util.Date.class, "time", false, "TIME");
        public final static Property Keepone = new Property(7, String.class, "keepone", false, "KEEPONE");
        public final static Property Keeptwo = new Property(8, String.class, "keeptwo", false, "KEEPTWO");
        public final static Property Keepthree = new Property(9, String.class, "keepthree", false, "KEEPTHREE");
    }


    public BorrowRecordDao(DaoConfig config) {
        super(config);
    }
    
    public BorrowRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BORROW_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"IS_FLAG\" INTEGER," + // 1: isFlag
                "\"PASSAGE\" TEXT," + // 2: passage
                "\"CARD\" TEXT," + // 3: card
                "\"BORROW\" INTEGER," + // 4: borrow
                "\"RESULT\" INTEGER," + // 5: result
                "\"TIME\" INTEGER," + // 6: time
                "\"KEEPONE\" TEXT," + // 7: keepone
                "\"KEEPTWO\" TEXT," + // 8: keeptwo
                "\"KEEPTHREE\" TEXT);"); // 9: keepthree
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BORROW_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BorrowRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Boolean isFlag = entity.getIsFlag();
        if (isFlag != null) {
            stmt.bindLong(2, isFlag ? 1L: 0L);
        }
 
        String passage = entity.getPassage();
        if (passage != null) {
            stmt.bindString(3, passage);
        }
 
        String card = entity.getCard();
        if (card != null) {
            stmt.bindString(4, card);
        }
 
        Boolean borrow = entity.getBorrow();
        if (borrow != null) {
            stmt.bindLong(5, borrow ? 1L: 0L);
        }
 
        Boolean result = entity.getResult();
        if (result != null) {
            stmt.bindLong(6, result ? 1L: 0L);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(7, time.getTime());
        }
 
        String keepone = entity.getKeepone();
        if (keepone != null) {
            stmt.bindString(8, keepone);
        }
 
        String keeptwo = entity.getKeeptwo();
        if (keeptwo != null) {
            stmt.bindString(9, keeptwo);
        }
 
        String keepthree = entity.getKeepthree();
        if (keepthree != null) {
            stmt.bindString(10, keepthree);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BorrowRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Boolean isFlag = entity.getIsFlag();
        if (isFlag != null) {
            stmt.bindLong(2, isFlag ? 1L: 0L);
        }
 
        String passage = entity.getPassage();
        if (passage != null) {
            stmt.bindString(3, passage);
        }
 
        String card = entity.getCard();
        if (card != null) {
            stmt.bindString(4, card);
        }
 
        Boolean borrow = entity.getBorrow();
        if (borrow != null) {
            stmt.bindLong(5, borrow ? 1L: 0L);
        }
 
        Boolean result = entity.getResult();
        if (result != null) {
            stmt.bindLong(6, result ? 1L: 0L);
        }
 
        java.util.Date time = entity.getTime();
        if (time != null) {
            stmt.bindLong(7, time.getTime());
        }
 
        String keepone = entity.getKeepone();
        if (keepone != null) {
            stmt.bindString(8, keepone);
        }
 
        String keeptwo = entity.getKeeptwo();
        if (keeptwo != null) {
            stmt.bindString(9, keeptwo);
        }
 
        String keepthree = entity.getKeepthree();
        if (keepthree != null) {
            stmt.bindString(10, keepthree);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BorrowRecord readEntity(Cursor cursor, int offset) {
        BorrowRecord entity = new BorrowRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0, // isFlag
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // passage
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // card
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // borrow
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // result
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // time
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // keepone
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // keeptwo
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // keepthree
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BorrowRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIsFlag(cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0);
        entity.setPassage(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCard(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBorrow(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setResult(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setTime(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setKeepone(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setKeeptwo(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setKeepthree(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BorrowRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BorrowRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BorrowRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
