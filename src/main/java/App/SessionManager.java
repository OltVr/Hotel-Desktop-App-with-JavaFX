package App;

import model.Room;
import model.User;

public class SessionManager {
    private static User currentUser;
    private static Room selectedRoom;

    public static void setUser(User user){
        currentUser=user;
    }

    public static User getUser(){
        return currentUser;
    }

    public static String getUserName(){
        if (currentUser != null){
            return currentUser.getFirstName();
        }else {
            return null;
        }
    }


    public static String getUserEmail(){
        if (currentUser!= null){
            return currentUser.getEmail();
        }
        return null;
    }


    //ROOM management
    public static void setRoom(Room room){
        selectedRoom=room;
    }


    public static int getSelectedRoomNumber(){
        if (selectedRoom!=null) {
            return selectedRoom.getRoomNumber();
        }
        return 0;
    }
    public static double getPrice(){
        if(selectedRoom!=null) {
            return selectedRoom.getPrice();
        }
        return 0;
    }

}
