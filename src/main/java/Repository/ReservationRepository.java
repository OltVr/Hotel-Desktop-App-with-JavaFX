package Repository;

import App.SessionManager;
import database.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Reservation;
import model.dto.CreateReservationDto;
import model.filter.ReservationFilter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    // ADMIN SIDE
    public static boolean deleteReservation(int reservationID) {
        String query = "DELETE FROM reservation WHERE reservationID = ?";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, reservationID);
            int result = pst.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1; // Default to -1 nese fails
        }
    }
    public static ObservableList<Reservation> searchReservations(String query, ReservationFilter filter) {
        ObservableList<Reservation> list = FXCollections.observableArrayList();
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            int paramIndex = 1;
            if (filter.getUserEmail() != null && !filter.getUserEmail().isEmpty()) {
                pst.setString(paramIndex++, filter.getUserEmail() + "%");
            }
            if (filter.getReservationID() != null) {
                pst.setInt(paramIndex++, filter.getReservationID());
            }

            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Reservation reservation = new Reservation(
                        result.getInt("reservationID"),
                        result.getString("email"),
                        result.getInt("roomNumber"),
                        result.getDate("reservationDate"),
                        result.getDate("checkInDate"),
                        result.getDate("checkOutDate"),
                        result.getInt("totalPrice"));
                list.add(reservation);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public static ObservableList<Reservation> ListReservations() {
        ObservableList<Reservation> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM reservation";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Reservation reservation = new Reservation(
                        result.getInt("reservationID"),
                        result.getString("email"),
                        result.getInt("roomNumber"),
                        result.getDate("reservationDate"),
                        result.getDate("checkInDate"),
                        result.getDate("checkOutDate"),
                        result.getInt("totalPrice"));
                list.add(reservation);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    // USER SIDE
    public static boolean reserve(CreateReservationDto reservationData) {
        Connection conn = DatabaseUtil.getConnection();
        String query = """
                INSERT INTO RESERVATION (email, roomNumber,checkInDate ,checkOutDate)
                VALUE (?, ?, ?, ?)
                """;

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, reservationData.getUserEmail());
            pst.setInt(2, reservationData.getRoomNumber());
            pst.setDate(3, Date.valueOf(reservationData.getCheckInDate()));
            pst.setDate(4, Date.valueOf(reservationData.getCheckOutDate()));
            pst.execute();
            pst.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[SQL] "+e.getMessage());
            return false;
        }

    }

    public static ObservableList<Reservation> listUserReservations() {
        ObservableList<Reservation> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM RESERVATION WHERE checkOutDate >= CURDATE() AND email = ?";
        Connection connection = DatabaseUtil.getConnection();
        try (
                PreparedStatement pst = connection.prepareStatement(query)) {
            String userEmail = SessionManager.getUserEmail();
            pst.setString(1, userEmail);
            try (ResultSet result = pst.executeQuery()) {
                while (result.next()) {
                    Reservation reservation = new Reservation(
                            result.getInt("reservationID"),
                            result.getString("email"),
                            result.getInt("roomNumber"),
                            result.getDate("reservationDate"),
                            result.getDate("checkInDate"),
                            result.getDate("checkOutDate"),
                            result.getDouble("totalPrice"));
                    list.add(reservation);
                }
            }
            return list;
        } catch (SQLException e) {
            System.out.println("[SQL RESER] " + e.getMessage());
            e.printStackTrace();
            return list;
        }
    }

    public static ObservableList<Reservation> listUserReservationHistory() {
        ObservableList<Reservation> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM RESERVATION WHERE checkOutDate < CURDATE() AND email = ?";
        Connection connection = DatabaseUtil.getConnection();
        try (
                PreparedStatement pst = connection.prepareStatement(query)) {
            String userEmail = SessionManager.getUserEmail();
            pst.setString(1, userEmail);
            try (ResultSet result = pst.executeQuery()) {
                while (result.next()) {
                    Reservation reservation = new Reservation(
                            result.getInt("reservationID"),
                            result.getString("email"),
                            result.getInt("roomNumber"),
                            result.getDate("reservationDate"),
                            result.getDate("checkInDate"),
                            result.getDate("checkOutDate"),
                            result.getDouble("totalPrice"));
                    list.add(reservation);
                }
            }
            return list;
        } catch (SQLException e) {
            System.out.println("[SQL HIST] " + e.getMessage());
            e.printStackTrace();
            return list;
        }
    }


    public static int getReservationCountForUser(String email){
        Connection connection = null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        int count = 0;

        try {
            String query="SELECT COUNT(*) AS reservation_count FROM reservation res JOIN user u ON res.email = u.email WHERE u.email = ?";
            connection=DatabaseUtil.getConnection();
            statement=connection.prepareStatement(query);
            statement.setString(1,email);
            resultSet=statement.executeQuery();

            if(resultSet.next()){
                count=resultSet.getInt("reservation_count");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

    public static boolean cancelReservation(int reservationID) {
        String query = "DELETE FROM RESERVATION WHERE reservationID = ?";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, reservationID);
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<Reservation> getReservationsForRoom(int roomNumber) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE roomNumber = ?";

        try {
            Connection connection = DatabaseUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, roomNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Reservation reservation = new Reservation(
                        result.getInt("reservationID"),
                        result.getString("email"),
                        result.getInt("roomNumber"),
                        result.getDate("reservationDate"),
                        result.getDate("checkInDate"),
                        result.getDate("checkOutDate"),
                        result.getDouble("totalPrice"));
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}
