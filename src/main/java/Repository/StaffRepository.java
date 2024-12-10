package Repository;

import database.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Staff;
import model.dto.InsertStaffDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffRepository {

    public static boolean insertStaff(InsertStaffDto staffData) {
        Connection conn = DatabaseUtil.getConnection();
        String query = """
            INSERT INTO STAFF (firstName, lastName, email, position, salary, isFullTime, hasBenefits)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, staffData.getStaffFirstName());
            pst.setString(2, staffData.getStaffLastName());
            pst.setString(3, staffData.getStaffEmail());
            pst.setString(4, staffData.getPosition());
            pst.setDouble(5, staffData.getSalary());
            pst.setBoolean(6, staffData.isFullTime());
            pst.setBoolean(7, staffData.isHasBenefits());
            pst.execute();
            pst.close();
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] SQL did not execute " + e.getMessage());
            return false;
        }
    }

    public static ObservableList<Staff> ListStaff(){
        ObservableList<Staff> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM staff";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Staff staff = new Staff(
                        result.getInt("staffID"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("position"),
                        result.getDouble("salary"),
                        result.getBoolean("isEmployed"),
                        result.getBoolean("isFullTime"),
                        result.getBoolean("hasBenefits"),
                        result.getTimestamp("createdAt"));
                list.add(staff);
            }
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    public static ObservableList<Staff> searchStaff(String query) {
        ObservableList<Staff> staffList = FXCollections.observableArrayList();
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Staff staff = new Staff(
                        result.getInt("staffID"),
                        result.getString("firstName"),
                        result.getString("lastName"),
                        result.getString("email"),
                        result.getString("position"),
                        result.getDouble("salary"),
                        result.getBoolean("isEmployed"),
                        result.getBoolean("isFullTime"),
                        result.getBoolean("hasBenefits"),
                        result.getTimestamp("createdAt")
                );
                staffList.add(staff);
            }
            return staffList;
        } catch (SQLException e) {
            e.printStackTrace();
            return staffList;
        }
    }

    public static boolean deleteStaff(String email) {
        String query = "DELETE FROM staff WHERE email = ?";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, email);
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }



}
