package com.seekersoftvendingapp.database;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;
import org.greenrobot.greendao.generator.ToMany;

/**
 * Created by kjh08490 on 2016/12/1.
 */

public class DaoUtil {

    public static void main(String[] args) throws Exception {
        creatTable();
    }

    private static void creatTable() {
        Schema schema = new Schema(1, "com.seekersoftvendingapp.database.table");

        addNote(schema);
        addAdminCard(schema);
        addEmployee(schema);
        addEmpPower(schema);
        addPassage(schema);
        addProduct(schema);

        addTakeoutRecord(schema);
        addSupplyRecord(schema);
        addBorrowRecord(schema);
        addErrorRecord(schema);

        try {
            new DaoGenerator().generateAll(schema, "../SeekerSoftVendingApp/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Note DEMO 测试
     *
     * @param schema
     */
    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("Note");
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }

    /**
     * public String card; // card id
     * public String objectId;
     * public String createdAt;
     * public String updatedAt;
     * <p>
     * 管理员表AdminCard --- cardid
     *
     * @param schema
     */
    private static void addAdminCard(Schema schema) {
        Entity adminCard = schema.addEntity("AdminCard");
        adminCard.addBooleanProperty("isDel");
        adminCard.addStringProperty("card").notNull();
        adminCard.addStringProperty("objectId").primaryKey();
        adminCard.addDateProperty("createdAt");
        adminCard.addDateProperty("updatedAt");
    }


    /**
     * public String empNo;
     * public ArrayList<String> card = new ArrayList<String>();
     * public ArrayList<String> power = new ArrayList<String>();
     * public String objectId;
     * public String createdAt;
     * public String updatedAt;
     * 员工  --  权限ids  --  卡号
     *
     * @param schema
     */
    private static void addEmployee(Schema schema) {
        Entity employee = schema.addEntity("Employee");
        employee.addBooleanProperty("isDel");
        employee.addStringProperty("empNo").notNull();
        employee.addStringProperty("card");
        employee.addStringProperty("power");
        employee.addStringProperty("objectId").primaryKey();
        employee.addDateProperty("createdAt");
        employee.addDateProperty("updatedAt");

    }

    /**
     * public String unit;
     * public BeginDate begin = new BeginDate();
     * public EmPowerProduct product = new EmPowerProduct();
     * public int count;
     * public int period;
     * public String objectId;
     * public String createdAt;
     * public String updatedAt;
     * 权限表  --  产品id
     *
     * @param schema
     */
    private static void addEmpPower(Schema schema) {
        Entity empPower = schema.addEntity("EmpPower");
        empPower.addBooleanProperty("isDel");
        empPower.addStringProperty("unit");
        empPower.addDateProperty("begin");
        empPower.addStringProperty("product");
        empPower.addIntProperty("count");
        empPower.addIntProperty("period");
        empPower.addStringProperty("objectId").primaryKey();
        empPower.addDateProperty("createdAt");
        empPower.addDateProperty("updatedAt");

    }


    /**
     * public int capacity;
     * public PassageProduct product = new PassageProduct();
     * public String seqNo;
     * public boolean borrowState;
     * public int stock;
     * public int whorlSize;
     * public boolean isSend;
     * public String objectId;
     * public String createdAt;
     * public String updatedAt;
     * 货道   ---    产品
     *
     * @param schema
     */
    private static void addPassage(Schema schema) {
        Entity passage = schema.addEntity("Passage");
        passage.addStringProperty("flag");// 副柜的 ABCD 的标识
        passage.addBooleanProperty("isDel");
        passage.addIntProperty("capacity");
        passage.addStringProperty("product");
        passage.addStringProperty("seqNo");
        passage.addBooleanProperty("borrowState");
        passage.addIntProperty("stock");
        passage.addIntProperty("whorlSize");
        passage.addBooleanProperty("isSend");
        passage.addStringProperty("objectId").primaryKey();
        passage.addDateProperty("createdAt");
        passage.addDateProperty("updatedAt");
    }


    /**
     * public String name;
     * public CustomProduct cusProduct = new CustomProduct();
     * public String objectId;
     * public String createdAt;
     * public String updatedAt;
     * 产品表  ---  客户产品的实际名称
     *
     * @param schema
     */
    private static void addProduct(Schema schema) {
        Entity product = schema.addEntity("Product");
        product.addBooleanProperty("isDel");
        product.addStringProperty("productName");
        product.addStringProperty("cusProductName");
        product.addStringProperty("objectId").primaryKey();
        product.addDateProperty("createdAt");
        product.addDateProperty("updatedAt");
    }


    /**
     * 取货消费记录接口
     */
    private static void addTakeoutRecord(Schema schema) {
        Entity takeoutRecord = schema.addEntity("TakeoutRecord");
        takeoutRecord.addIdProperty().primaryKey();
        takeoutRecord.addBooleanProperty("isDel");
        takeoutRecord.addStringProperty("passage");
        takeoutRecord.addStringProperty("card");
        takeoutRecord.addStringProperty("productId");
        takeoutRecord.addDateProperty("time");
    }


    /**
     * 补货记录接口
     */
    private static void addSupplyRecord(Schema schema) {
        Entity supplyRecord = schema.addEntity("SupplyRecord");
        supplyRecord.addIdProperty().primaryKey();
        supplyRecord.addBooleanProperty("isFlag");
        supplyRecord.addStringProperty("passage");
        supplyRecord.addStringProperty("card");
        supplyRecord.addIntProperty("count");
        supplyRecord.addStringProperty("time");
    }


    /**
     * 借还记录接口
     */
    private static void addBorrowRecord(Schema schema) {
        Entity borrowRecord = schema.addEntity("BorrowRecord");
        borrowRecord.addIdProperty().primaryKey();
        borrowRecord.addBooleanProperty("isFlag");
        borrowRecord.addStringProperty("passage");
        borrowRecord.addStringProperty("card");
        borrowRecord.addBooleanProperty("borrow");
        borrowRecord.addDateProperty("time");
    }


    /**
     * 提交异常记录
     *
     * @param schema
     */
    private static void addErrorRecord(Schema schema) {
        Entity errorRecord = schema.addEntity("ErrorRecord");
        errorRecord.addIdProperty().primaryKey();
        errorRecord.addBooleanProperty("isFlag");
        errorRecord.addStringProperty("passage");
        errorRecord.addStringProperty("card");
        errorRecord.addStringProperty("node");
        errorRecord.addStringProperty("info");
        errorRecord.addStringProperty("time");
    }

    private static void addCustomerOrder(Schema schema) {
        // customer
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();

        // order
        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.addIdProperty();
        Property orderDate = order.addDateProperty("date").getProperty();
        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
        order.addToOne(customer, customerId);
        ToMany customerToOrders = customer.addToMany(order, customerId);
        customerToOrders.setName("orders");
        customerToOrders.orderAsc(orderDate);
    }
}