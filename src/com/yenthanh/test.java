/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yenthanh;

import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 *
 * @author Admin
 */
public class test {

    public test() {
    }

    public static void main(String args[]) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn
                    = DriverManager.getConnection("jdbc:mysql://localhost/cloudsim?"
                            + "user=occbuu&password=123456");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `thanhresults`");

            while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
                int id = rs.getInt(1);
                String AlgoName = rs.getString(2);
                int noCloudlets = rs.getInt("noCloudlets");
                System.out.println("--------------------");
                System.out.println("id::" + id);
                System.out.println("Name:" + AlgoName);
                System.out.println("no Cloudlets:" + noCloudlets);
            }
            // Đóng kết nối
            conn.close();

            // Now do something with the ResultSet ....
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }
    }
}
