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
 * DAO for table "PRODUCT".
*/
public class ProductDao extends AbstractDao<Product, String> {

    public static final String TABLENAME = "PRODUCT";

    /**
     * Properties of entity Product.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property IsDel = new Property(0, Boolean.class, "isDel", false, "IS_DEL");
        public final static Property ProductName = new Property(1, String.class, "productName", false, "PRODUCT_NAME");
        public final static Property CusProductName = new Property(2, String.class, "cusProductName", false, "CUS_PRODUCT_NAME");
        public final static Property ObjectId = new Property(3, String.class, "objectId", true, "OBJECT_ID");
        public final static Property CreatedAt = new Property(4, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(5, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    }


    public ProductDao(DaoConfig config) {
        super(config);
    }
    
    public ProductDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCT\" (" + //
                "\"IS_DEL\" INTEGER," + // 0: isDel
                "\"PRODUCT_NAME\" TEXT," + // 1: productName
                "\"CUS_PRODUCT_NAME\" TEXT," + // 2: cusProductName
                "\"OBJECT_ID\" TEXT PRIMARY KEY NOT NULL ," + // 3: objectId
                "\"CREATED_AT\" INTEGER," + // 4: createdAt
                "\"UPDATED_AT\" INTEGER);"); // 5: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Product entity) {
        stmt.clearBindings();
 
        Boolean isDel = entity.getIsDel();
        if (isDel != null) {
            stmt.bindLong(1, isDel ? 1L: 0L);
        }
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(2, productName);
        }
 
        String cusProductName = entity.getCusProductName();
        if (cusProductName != null) {
            stmt.bindString(3, cusProductName);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(4, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(5, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(6, updatedAt.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Product entity) {
        stmt.clearBindings();
 
        Boolean isDel = entity.getIsDel();
        if (isDel != null) {
            stmt.bindLong(1, isDel ? 1L: 0L);
        }
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(2, productName);
        }
 
        String cusProductName = entity.getCusProductName();
        if (cusProductName != null) {
            stmt.bindString(3, cusProductName);
        }
 
        String objectId = entity.getObjectId();
        if (objectId != null) {
            stmt.bindString(4, objectId);
        }
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(5, createdAt.getTime());
        }
 
        java.util.Date updatedAt = entity.getUpdatedAt();
        if (updatedAt != null) {
            stmt.bindLong(6, updatedAt.getTime());
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
    }    

    @Override
    public Product readEntity(Cursor cursor, int offset) {
        Product entity = new Product( //
            cursor.isNull(offset + 0) ? null : cursor.getShort(offset + 0) != 0, // isDel
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // productName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // cusProductName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // objectId
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // createdAt
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)) // updatedAt
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Product entity, int offset) {
        entity.setIsDel(cursor.isNull(offset + 0) ? null : cursor.getShort(offset + 0) != 0);
        entity.setProductName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCusProductName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setObjectId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCreatedAt(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setUpdatedAt(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(Product entity, long rowId) {
        return entity.getObjectId();
    }
    
    @Override
    public String getKey(Product entity) {
        if(entity != null) {
            return entity.getObjectId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Product entity) {
        return entity.getObjectId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
