package com.seekersoftvendingapp.database.table;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig noteDaoConfig;
    private final DaoConfig adminCardDaoConfig;
    private final DaoConfig employeeDaoConfig;
    private final DaoConfig empPowerDaoConfig;
    private final DaoConfig passageDaoConfig;
    private final DaoConfig productDaoConfig;
    private final DaoConfig takeoutRecordDaoConfig;
    private final DaoConfig supplyRecordDaoConfig;
    private final DaoConfig borrowRecordDaoConfig;
    private final DaoConfig errorRecordDaoConfig;

    private final NoteDao noteDao;
    private final AdminCardDao adminCardDao;
    private final EmployeeDao employeeDao;
    private final EmpPowerDao empPowerDao;
    private final PassageDao passageDao;
    private final ProductDao productDao;
    private final TakeoutRecordDao takeoutRecordDao;
    private final SupplyRecordDao supplyRecordDao;
    private final BorrowRecordDao borrowRecordDao;
    private final ErrorRecordDao errorRecordDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        noteDaoConfig = daoConfigMap.get(NoteDao.class).clone();
        noteDaoConfig.initIdentityScope(type);

        adminCardDaoConfig = daoConfigMap.get(AdminCardDao.class).clone();
        adminCardDaoConfig.initIdentityScope(type);

        employeeDaoConfig = daoConfigMap.get(EmployeeDao.class).clone();
        employeeDaoConfig.initIdentityScope(type);

        empPowerDaoConfig = daoConfigMap.get(EmpPowerDao.class).clone();
        empPowerDaoConfig.initIdentityScope(type);

        passageDaoConfig = daoConfigMap.get(PassageDao.class).clone();
        passageDaoConfig.initIdentityScope(type);

        productDaoConfig = daoConfigMap.get(ProductDao.class).clone();
        productDaoConfig.initIdentityScope(type);

        takeoutRecordDaoConfig = daoConfigMap.get(TakeoutRecordDao.class).clone();
        takeoutRecordDaoConfig.initIdentityScope(type);

        supplyRecordDaoConfig = daoConfigMap.get(SupplyRecordDao.class).clone();
        supplyRecordDaoConfig.initIdentityScope(type);

        borrowRecordDaoConfig = daoConfigMap.get(BorrowRecordDao.class).clone();
        borrowRecordDaoConfig.initIdentityScope(type);

        errorRecordDaoConfig = daoConfigMap.get(ErrorRecordDao.class).clone();
        errorRecordDaoConfig.initIdentityScope(type);

        noteDao = new NoteDao(noteDaoConfig, this);
        adminCardDao = new AdminCardDao(adminCardDaoConfig, this);
        employeeDao = new EmployeeDao(employeeDaoConfig, this);
        empPowerDao = new EmpPowerDao(empPowerDaoConfig, this);
        passageDao = new PassageDao(passageDaoConfig, this);
        productDao = new ProductDao(productDaoConfig, this);
        takeoutRecordDao = new TakeoutRecordDao(takeoutRecordDaoConfig, this);
        supplyRecordDao = new SupplyRecordDao(supplyRecordDaoConfig, this);
        borrowRecordDao = new BorrowRecordDao(borrowRecordDaoConfig, this);
        errorRecordDao = new ErrorRecordDao(errorRecordDaoConfig, this);

        registerDao(Note.class, noteDao);
        registerDao(AdminCard.class, adminCardDao);
        registerDao(Employee.class, employeeDao);
        registerDao(EmpPower.class, empPowerDao);
        registerDao(Passage.class, passageDao);
        registerDao(Product.class, productDao);
        registerDao(TakeoutRecord.class, takeoutRecordDao);
        registerDao(SupplyRecord.class, supplyRecordDao);
        registerDao(BorrowRecord.class, borrowRecordDao);
        registerDao(ErrorRecord.class, errorRecordDao);
    }
    
    public void clear() {
        noteDaoConfig.clearIdentityScope();
        adminCardDaoConfig.clearIdentityScope();
        employeeDaoConfig.clearIdentityScope();
        empPowerDaoConfig.clearIdentityScope();
        passageDaoConfig.clearIdentityScope();
        productDaoConfig.clearIdentityScope();
        takeoutRecordDaoConfig.clearIdentityScope();
        supplyRecordDaoConfig.clearIdentityScope();
        borrowRecordDaoConfig.clearIdentityScope();
        errorRecordDaoConfig.clearIdentityScope();
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public AdminCardDao getAdminCardDao() {
        return adminCardDao;
    }

    public EmployeeDao getEmployeeDao() {
        return employeeDao;
    }

    public EmpPowerDao getEmpPowerDao() {
        return empPowerDao;
    }

    public PassageDao getPassageDao() {
        return passageDao;
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public TakeoutRecordDao getTakeoutRecordDao() {
        return takeoutRecordDao;
    }

    public SupplyRecordDao getSupplyRecordDao() {
        return supplyRecordDao;
    }

    public BorrowRecordDao getBorrowRecordDao() {
        return borrowRecordDao;
    }

    public ErrorRecordDao getErrorRecordDao() {
        return errorRecordDao;
    }

}
