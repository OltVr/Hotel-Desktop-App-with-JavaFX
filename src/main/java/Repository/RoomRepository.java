package Repository;

import database.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Room;
import model.dto.InsertRoomDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRepository {
    //ADMIN SIDE
    public static boolean insertRoom(InsertRoomDto roomData) {
        Connection conn = DatabaseUtil.getConnection();
        String query = """
                INSERT INTO ROOMS (roomNumber, floorNumber, roomType, capacity, bedNumber, price)
                VALUE (?, ?, ?, ?, ?, ?)
                """;

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, roomData.getRoomNumber());
            pst.setInt(2, roomData.getFloorNumber());
            pst.setString(3, roomData.getRoomType());
            pst.setInt(4, roomData.getCapacity());
            pst.setInt(5, roomData.getBedNumber());
            pst.setDouble(6, roomData.getPrice());
            pst.execute();
            pst.close();
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] SQL did not execute " + e.getMessage());
            return false;
        }

    }



    private static Room getRoomFromResultSet(ResultSet result) {
        try {
            int roomNumber = result.getInt("roomNumber");
            int floor = result.getInt("floorNumber");
            int capacity = result.getInt("capacity");
            int beds = result.getInt("bedNumber");
            double price = result.getDouble("price");
            boolean isAvailable = result.getBoolean("isAvailable");
            String roomType = result.getString("roomType");
            return new Room(
                    roomNumber, floor, roomType, capacity, beds, price, isAvailable
            );
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            return null;
        }
    }
    public static Room getRoom(int roomNumber, int floor) {
        String query = "SELECT * FROM ROOMS WHERE roomNumber = ? AND floorNumber= ? LIMIT 1";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, roomNumber);
            pst.setInt(2, floor);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                return getRoomFromResultSet(result);
            }
            ;
            pst.close();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean deleteRoom(int roomNumber, int floor) {
        String query = "DELETE FROM ROOMS WHERE roomNumber = ? AND floorNumber = ?";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, roomNumber);
            pst.setInt(2, floor);
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateRoom(InsertRoomDto room) {
        String query = "UPDATE ROOMS SET floorNumber= ?, roomType= ?, capacity= ?, bedNumber= ?, price= ?  WHERE roomNumber = ?";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, room.getFloorNumber());
            pst.setString(2 ,room.getRoomType());
            pst.setInt(3, room.getCapacity());
            pst.setInt(4, room.getBedNumber());
            pst.setDouble(5, room.getPrice());
            pst.setInt(6, room.getRoomNumber());
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<Room> ListRoom() {
        ObservableList<Room> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM ROOMS";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Room room = new Room(result.getInt("roomNumber"),
                        result.getInt("floorNumber"),
                        result.getString("roomType"),
                        result.getInt("capacity"),
                        result.getInt("bedNumber"),
                        result.getDouble("price"),
                        result.getBoolean("isAvailable"));

                list.add(room);
            };
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    public static int countRoomsBookedToday() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String query = "SELECT COUNT(*) as Rooms_booked FROM reservation WHERE DATE(reservationDate) = CURDATE()";
            connection = DatabaseUtil.getConnection();
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();
            if (result.next()) {

                return result.getInt("Rooms_booked");
            }
            return 0;

        } catch (SQLException e) {
            System.out.println("[ERROR DB]" + e.getMessage());
            return 0;
        }
    }

    // USER SIDE
    public static ObservableList<Room> listSeaViewRooms() {
        ObservableList<Room> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM rooms WHERE roomType = 'Sea View'";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Room room = new Room(
                        result.getInt("roomNumber"),
                        result.getInt("floorNumber"),
                        result.getString("roomType"),
                        result.getInt("capacity"),
                        result.getInt("bedNumber"),
                        result.getDouble("price"),
                        result.getBoolean("isAvailable"));
                list.add(room);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public static ObservableList<Room> listCityViewRooms() {
        ObservableList<Room> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM rooms WHERE roomType = 'City View'";
        Connection connection = DatabaseUtil.getConnection();
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                Room room = new Room(
                        result.getInt("roomNumber"),
                        result.getInt("floorNumber"),
                        result.getString("roomType"),
                        result.getInt("capacity"),
                        result.getInt("bedNumber"),
                        result.getDouble("price"),
                        result.getBoolean("isAvailable"));
                list.add(room);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }




}
