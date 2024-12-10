package Repository;

import database.DatabaseUtil;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRepository {

    public static int calculateTotalIncome(){
        Connection con=null;
        PreparedStatement statement=null;
        ResultSet rez=null;

        try{
            String Query= "SELECT SUM(DATEDIFF(res.checkOutDate, res.checkInDate) * r.price) AS total_price FROM rooms r JOIN reservation res ON r.roomNumber = res.roomNumber";
            con= DatabaseUtil.getConnection();
            statement=con.prepareStatement(Query);
            rez=statement.executeQuery();

            if (rez.next()){
                int price= rez.getInt("total_price");
                return price;
            }
            return 0;
        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static ResultSet getDailyIncomeData() throws SQLException {
        Connection connect = DatabaseUtil.getConnection();
        String query = "SELECT res.reservationDate, SUM(DATEDIFF(res.checkOutDate, res.checkInDate) * r.price) AS total_price " +
                "FROM rooms r JOIN reservation res ON r.roomNumber = res.roomNumber " +
                "WHERE res.reservationDate >= CURDATE() - INTERVAL 7 DAY " +
                "GROUP BY res.reservationDate";
        PreparedStatement statement = connect.prepareStatement(query);
        return statement.executeQuery();
    }
}
