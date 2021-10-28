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
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class dbUtil {
    public  Connection conn;
    

    public dbUtil() {
        this.conn= null;   
    }

    public void connect() {
        try {
            this.conn
                    = DriverManager.getConnection("jdbc:mysql://localhost/cloudsim?"
                            + "user=occbuu&password=123456");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            this.conn = null;
        }
    }

    public void disconnect() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            this.conn = null;
        }
        this.conn = null;
    }

    public ResultSet query(String query) throws SQLException {
        if (!this.isConnected()) {
            this.connect();
        }

        Statement sm = this.conn.createStatement();

        return sm.executeQuery(query);
    }

    public int updateQuery(String query) throws SQLException {
        if (this.isConnected()) {
            this.connect();
        }

        Statement sm = this.conn.createStatement();

        return sm.executeUpdate(query);
    }

    public boolean isConnected() {
        try {
            return this.conn != null && !this.conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

}
