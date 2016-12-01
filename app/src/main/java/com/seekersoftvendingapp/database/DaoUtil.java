package com.seekersoftvendingapp.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

/**
 * Created by kjh08490 on 2016/12/1.
 */

public class DaoUtil {

    public static void main(String[] args) throws Exception {
        creatTable();
    }

    private static void creatTable() {
        Schema schema = new Schema(1, "com.seekersoftvendingapp.database.maincard");
        addAdminCard(schema);
         addCustomerOrder(schema);
        try {
            new DaoGenerator().generateAll(schema, "../SeekerSoftVendingApp/app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* public String card; // card id
     public String objectId;
     public String createdAt;
     public String updatedAt;*/
    private static void addAdminCard(Schema schema) {
        Entity note = schema.addEntity("AdminCard");
        note.addIdProperty();
        note.addStringProperty("card").notNull();
        note.addStringProperty("objectId");
        note.addDateProperty("updatedAt");
    }

    private static void addCustomerOrder(Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();
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
