package com.seekersoftvendingapp.database;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

/**
 * Created by kjh08490 on 2016/12/1.
 */

public class DaoUtil {

    public static void main(String[] args) throws Exception {
        creatTable();
    }

    private static void creatTable() {
        Schema schema = new Schema(1, "com.seekersoftvendingapp.database.table");

        addEmpCard(schema);
        addAdminCard(schema);
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

    public static void addEmpCard(Schema schema) {
        Entity empCard = schema.addEntity("EmpCard");
        empCard.addStringProperty("card").notNull();
        empCard.addStringProperty("emp");
        empCard.addBooleanProperty("isDel");
        empCard.addStringProperty("createdAt");
        empCard.addStringProperty("updatedAt");
        empCard.addStringProperty("objectId");
        empCard.addStringProperty("keepone");
        empCard.addStringProperty("keeptwo");
        empCard.addStringProperty("keepthree");
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
        adminCard.addStringProperty("createdAt");
        adminCard.addStringProperty("updatedAt");
        adminCard.addStringProperty("keepone");
        adminCard.addStringProperty("keeptwo");
        adminCard.addStringProperty("keepthree");
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
        empPower.addStringProperty("emp");
        empPower.addStringProperty("unit");
        empPower.addBooleanProperty("isDel");
        empPower.addStringProperty("product");
        empPower.addIntProperty("count");
        empPower.addIntProperty("period");
        empPower.addIntProperty("used");
        empPower.addStringProperty("objectId").primaryKey();
        empPower.addStringProperty("createdAt");
        empPower.addStringProperty("updatedAt");

        empPower.addStringProperty("keepone");
        empPower.addStringProperty("keeptwo");
        empPower.addStringProperty("keepthree");

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
        passage.addIntProperty("capacity");
        passage.addBooleanProperty("isDel");
        passage.addStringProperty("seqNo");
        passage.addStringProperty("used");// 匹配的是empcard中的emp
        passage.addStringProperty("product");
        passage.addBooleanProperty("borrowState");
        passage.addIntProperty("stock");
        passage.addBooleanProperty("isSend");

        passage.addStringProperty("flag");// 副柜的 ABCD 的标识
        passage.addIntProperty("whorlSize");

        passage.addStringProperty("borrowUser");

        passage.addStringProperty("objectId").primaryKey();
        passage.addStringProperty("createdAt");
        passage.addStringProperty("updatedAt");
        passage.addStringProperty("keepone");
        passage.addStringProperty("keeptwo");
        passage.addStringProperty("keepthree");
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
        product.addStringProperty("createdAt");
        product.addStringProperty("updatedAt");
        product.addStringProperty("keepone");
        product.addStringProperty("keeptwo");
        product.addStringProperty("keepthree");
    }


    /**
     * 取货消费记录接口
     */
    private static void addTakeoutRecord(Schema schema) {
        Entity takeoutRecord = schema.addEntity("TakeoutRecord");
        takeoutRecord.addIdProperty().primaryKey();
        takeoutRecord.addBooleanProperty("isDel");// true : 已经同步； false：未同步
        takeoutRecord.addStringProperty("passage");
        takeoutRecord.addStringProperty("card");
        takeoutRecord.addStringProperty("productId");
        takeoutRecord.addStringProperty("time");
        takeoutRecord.addIntProperty("count");
        takeoutRecord.addStringProperty("keepone");
        takeoutRecord.addStringProperty("keeptwo");
        takeoutRecord.addStringProperty("keepthree");
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
        supplyRecord.addStringProperty("keepone");
        supplyRecord.addStringProperty("keeptwo");
        supplyRecord.addStringProperty("keepthree");
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
        borrowRecord.addBooleanProperty("result");//示这条取货记录是成功有效的
        borrowRecord.addStringProperty("time");
        borrowRecord.addStringProperty("keepone");
        borrowRecord.addStringProperty("keeptwo");
        borrowRecord.addStringProperty("keepthree");
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
        errorRecord.addStringProperty("keepone");
        errorRecord.addStringProperty("keeptwo");
        errorRecord.addStringProperty("keepthree");
    }
}
