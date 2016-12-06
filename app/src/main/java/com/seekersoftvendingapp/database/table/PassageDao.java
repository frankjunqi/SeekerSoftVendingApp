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
 * DAO for table "PASSAGE".
*/
public class PassageDao extends AbstractDao<Passage, Long> {

    public static final String TABLENAME = "PASSAGE";

    /**
     * Properties of entity Passage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Capacity = new Property(1, Integer.class, "capacity", false, "CAPACITY");
        public final static Property Product = new Property(2, String.class, "product", false, "PRODUCT");
        public final static Property SeqNo = new Property(3, String.class, "seqNo", false, "SEQ_NO");
        public final static Property BorrowState = new Property(4, Boolean.class, "borrowState", false, "BORROW_STATE");
        public final static Property Stock = new Property(5, Integer.class, "stock", false, "STOCK");
        public final static Property WhorlSize = new Property(6, Integer.class, "whorlSize", false, "WHORL_SIZE");
        public final static Property IsSend = new Property(7, Boolean.class, "isSend", false, "IS_SEND");
        public final static Property ObjectId = new Property(8, String.class, "objectId", false, "OBJECT_ID");
        public final static Property CreatedAt = new Property(9, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(10, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    }


    public PassageDao(DaoConfig config) {
        super(config);
    }
    
    public PassageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PASSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CAPACITY\" INTEGER," + // 1: capacity
                "\"PRODUCT\" TEXT," + // 2: product
                "\"SEQ_NO\" TEXT," + // 3: seqNo
                "\"BORROW_STATE\" INTEGER," + // 4: borrowState
                "\"STOCK\" INTEGER," + // 5: stock
                "\"WHORL_SIZE\" INTEGER," + // 6: whorlSize
                "\"IS_SEND\" INTEGER," + // 7: isSend
                "\"OBJECT_ID\" TEXT," + // 8: objectId
                "\"CREATED_AT\" INTEGER," + // 9: createdAt
                "\"UPDATED_AT\" INTEGER);"); // 10: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PASSAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Passage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer capacity = entity.getCapacity();
        if (capacity != null) {
            stmt.bindLong(2, capacity);
        }
 
        String product = entity.getProduct();
        if (product != null) {
            stmt.bindString(3, product);
        }
 
        String seqNo = entity.getSeqNo();
        if (seqNo != null) {
            stmt.bindString(4, seqNo);
        }
 
        Boolean borrowState = entity.getBorrowState();
        if (borrowState != null) {
            stmt.bindLong(5, borrowState ? 1L: 0L);
        }
 
        Integer stock = entity.getStock();
        if (stock != null) {
            stmt.bindLong(6, stock);
        }
 
        Integer whorlSize = entity.getWhorlSize();
        if (whorlSize != null) {
            stmt.bindLong(7, whorlSize);
        }
 
        Boolean isSend = entity.getIsSend();
        if (isSend != null) {
            stmt.bindLong(8, isSend ? 1L: 0L);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(9, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(10, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(11, updatedAt.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Passage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer capacity = entity.getCapacity();
        if (capacity != null) {
            stmt.bindLong(2, capacity);
        }
 
        String product = entity.getProduct();
        if (product != null) {
            stmt.bindString(3, product);
        }
 
        String seqNo = entity.getSeqNo();
        if (seqNo != null) {
            stmt.bindString(4, seqNo);
        }
 
        Boolean borrowState = entity.getBorrowState();
        if (borrowState != null) {
            stmt.bindLong(5, borrowState ? 1L: 0L);
        }
 
        Integer stock = entity.getStock();
        if (stock != null) {
            stmt.bindLong(6, stock);
        }
 
        Integer whorlSize = entity.getWhorlSize();
        if (whorlSize != null) {
            stmt.bindLong(7, whorlSize);
        }
 
        Boolean isSend = entity.getIsSend();
        if (isSend != null) {
            stmt.bindLong(8, isSend ? 1L: 0L);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(9, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(10, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(11, updatedAt.getTime());
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Passage readEntity(Cursor cursor, int offset) {
        Passage entity = new Passage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // capacity
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // product
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // seqNo
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // borrowState
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // stock
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // whorlSize
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // isSend
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // objectId
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // createdAt
            cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)) // updatedAt
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Passage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCapacity(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setProduct(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSeqNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBorrowState(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setStock(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setWhorlSize(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setIsSend(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setObjectId(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCreatedAt(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setUpdatedAt(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Passage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Passage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Passage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
