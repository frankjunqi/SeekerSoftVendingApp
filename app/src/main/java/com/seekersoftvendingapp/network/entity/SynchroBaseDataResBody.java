package com.seekersoftvendingapp.network.entity;

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.Employee;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.network.entity.basedata.AdminCardEntity;
import com.seekersoftvendingapp.network.entity.basedata.EmpPowerEntity;
import com.seekersoftvendingapp.network.entity.basedata.EmployeeEntity;
import com.seekersoftvendingapp.network.entity.basedata.PassageEntity;
import com.seekersoftvendingapp.network.entity.basedata.ProductEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class SynchroBaseDataResBody implements Serializable {

    public int status;// 服务器返回的状态码
    public String msg;// 服务器返回具体状态码描述信息
    public SyncharBaseEntity data = new SyncharBaseEntity();
    public String server_time;// 返回服务器时间

    public List<AdminCard> getAdminCardList() {
        ArrayList<AdminCard> adminCards = new ArrayList<AdminCard>();
        if (data.AdminCard != null) {
            for (AdminCardEntity adminCardEntity : data.AdminCard) {
                adminCards.add(adminCardEntity.getAdminCard());
            }
        }
        return adminCards;
    }

    public List<Product> getProductList() {
        ArrayList<Product> products = new ArrayList<Product>();
        if (data.Product != null) {
            for (ProductEntity productEntity : data.Product) {
                products.add(productEntity.getProduct());
            }
        }
        return products;
    }

    public List<Employee> getEmployeeList() {
        List<Employee> employees = new ArrayList<Employee>();
        if (data.Employee != null) {
            for (EmployeeEntity employeeEntity : data.Employee) {
                employees.add(employeeEntity.getEmployee());
            }
        }
        return employees;
    }

    public List<EmpPower> getEmpPowerList() {
        List<EmpPower> emppowers = new ArrayList<EmpPower>();
        if (data.EmpPower != null) {
            for (EmpPowerEntity emppowerEntity : data.EmpPower) {
                emppowers.add(emppowerEntity.getEmpPower());
            }
        }
        return emppowers;
    }

    public List<Passage> getPassageList() {
        List<Passage> passages = new ArrayList<Passage>();
        if (data.Passage != null) {
            for (PassageEntity passageEntity : data.Passage) {
                passages.add(passageEntity.getPassage());
            }
        }
        return passages;
    }

    public class SyncharBaseEntity implements Serializable {

        public List<AdminCardEntity> AdminCard = new ArrayList<AdminCardEntity>();
        public List<ProductEntity> Product = new ArrayList<ProductEntity>();
        public List<EmployeeEntity> Employee = new ArrayList<EmployeeEntity>();
        public List<EmpPowerEntity> EmpPower = new ArrayList<EmpPowerEntity>();
        public List<PassageEntity> Passage = new ArrayList<PassageEntity>();

        @Override
        public String toString() {
            String admincard = "";
            String product = "";
            String employee = "";
            String emppower = "";
            String passage = "";
            for (int i = 0; i < AdminCard.size(); i++) {
                admincard = admincard + AdminCard.get(i).toString();
            }

            for (int i = 0; i < Product.size(); i++) {
                product = product + Product.get(i).toString();
            }

            for (int i = 0; i < Employee.size(); i++) {
                employee = employee + Employee.get(i).toString();
            }

            for (int i = 0; i < EmpPower.size(); i++) {
                emppower = emppower + EmpPower.get(i).toString();
            }

            for (int i = 0; i < Passage.size(); i++) {
                passage = passage + Passage.get(i).toString();
            }

            return "{ \nAdminCard = " + admincard + ",\nProduct" + product + ",\nEmployee = " + employee + ",\nEmpPower = " + emppower + ",\nPassage = " + passage + "}";
        }
    }

    @Override
    public String toString() {
        return "{ status = " + status + ",\nmsg = " + msg + ",\nserver_time = " + server_time + ",\ndata = " + data.toString() + "}";
    }
}
