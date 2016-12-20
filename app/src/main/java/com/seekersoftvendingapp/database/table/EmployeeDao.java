package com.seekersoftvendingapp.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EMPLOYEE".
*/
public class EmployeeDao extends AbstractDao<Employee, String> {

    public static final String TABLENAME = "EMPLOYEE";

    /**
     * Properties of entity Employee.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property IsDel = new Property(0, Boolean.class, "isDel", false, "IS_DEL");
        public final static Property EmpNo = new Property(1, String.class, "empNo", false, "EMP_NO");
        public final static Property Card = new Property(2, String.class, "card", false, "CARD");
        public final static Property Power = new Property(3, String.class, "power", false, "POWER");
        public final static Property ObjectId = new Property(4, String.class, "objectId", true, "OBJECT_ID");
        public final static Property CreatedAt = new Property(5, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(6, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    }


    public EmployeeDao(DaoConfig config) {
        super(config);
    }
    
    public EmployeeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EMPLOYEE\" (" + //
                "\"IS_DEL\" INTEGER," + // 0: isDel
                "\"EMP_NO\" TEXT NOT NULL ," + // 1: empNo
                "\"CARD\" TEXT," + // 2: card
                "\"POWER\" TEXT," + // 3: power
                "\"OBJECT_ID\" TEXT PRIMARY KEY NOT NULL ," + // 4: objectId
                "\"CREATED_AT\" INTEGER," + // 5: createdAt
                "\"UPDATED_AT\" INTEGER);"); // 6: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EMPLOYEE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Employee entity) {
        stmt.clearBindings();
 
        Boolean isDel = entity.getIsDel();
        if (isDel != null) {
            stmt.bindLong(1, isDel ? 1L: 0L);
        }
        stmt.bindString(2, entity.getEmpNo());
 
        String card = entity.getCard();
        if (card != null) {
            stmt.bindString(3, card);
        }
 
        String power = entity.getPower();
        if (power != null) {
            stmt.bindString(4, power);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(5, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(6, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(7, updatedAt.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Employee entity) {
        stmt.clearBindings();
 
        Boolean isDel = entity.getIsDel();
        if (isDel != null) {
            stmt.bindLong(1, isDel ? 1L: 0L);
        }
        stmt.bindString(2, entity.getEmpNo());
 
        String card = entity.getCard();
        if (card != null) {
            stmt.bindString(3, card);
        }
 
        String power = entity.getPower();
        if (power != null) {
            stmt.bindString(4, power);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(5, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(6, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(7, updatedAt.getTime());
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
    }    

    @Override
    public Employee readEntity(Cursor cursor, int offset) {
        Employee entity = new Employee( //
            cursor.isNull(offset + 0) ? null : cursor.getShort(offset + 0) != 0, // isDel
            cursor.getString(offset + 1), // empNo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // card
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // power
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // objectId
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // createdAt
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)) // updatedAt
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Employee entity, int offset) {
        entity.setIsDel(cursor.isNull(offset + 0) ? null : cursor.getShort(offset + 0) != 0);
        entity.setEmpNo(cursor.getString(offset + 1));
        entity.setCard(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPower(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setObjectId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreatedAt(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdatedAt(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Employee entity, long rowId) {
        return entity.getObjectId();
    }
    
    @Override
    public String getKey(Employee entity) {
        if(entity != null) {
            return entity.getObjectId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Employee entity) {
        return entity.getObjectId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
