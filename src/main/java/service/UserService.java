package service;

import Repository.ReservationRepository;
import Repository.RoomRepository;
import javafx.collections.ObservableList;
import model.Reservation;
import model.Room;
import model.User;
import model.dto.CreateReservationDto;
import model.dto.LoginUserDto;
import Repository.UserRepository;
import model.dto.CreateUserDto;
import model.dto.UserDto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UserService {
    public static boolean signUp(UserDto userData){
        String password=userData.getPassword();
        String confirmPassword=userData.getConfirmPassword();

        if(!password.equals(confirmPassword)){
            return false;
        }
        String salt= PasswordHasher.generateSalt();
        String passwordHash=PasswordHasher.generateSaltedHash(password, salt);

        CreateUserDto createUserData = new CreateUserDto(
                userData.getFirstName(),
                userData.getLastName(),
                userData.getEmail(),
                salt,
                passwordHash
        );
    return UserRepository.createUser(createUserData);
    }

    public static boolean login( LoginUserDto loginData){
        User user = UserRepository.getUserByEmail(loginData.getEmail());
        if(user == null){
            return false;
        }

        String password = loginData.getPassword();
        String salt = user.getSalt();
        String passwordHash = user.getPasswordHash();


        return PasswordHasher.compareSaltedHash(
                password, salt, passwordHash
        );
    }

    public static boolean loginAdmin( LoginUserDto loginData){
        if (login(loginData)){
            User user = UserRepository.getUserByEmail(loginData.getEmail());
            if(user == null){
                return false;
            }
            return user.isAdmin();
        }
        return false;
    }

    public static User getUserEmail(String email){
        return UserRepository.getUserByEmail(email);
    }

    public static boolean cancelReservation(int reservationID){
        return ReservationRepository.cancelReservation(reservationID);
    }

    public static boolean userExists(String email) {
        return UserRepository.userExists(email);
    }

    public static ObservableList<Room> listSeaViewRooms() {
        return RoomRepository.listSeaViewRooms();
    }

    public static ObservableList<Room> listCityViewRooms() {
        return RoomRepository.listCityViewRooms();
    }

    public static double totalPrice(LocalDate start, LocalDate end, double price){
        try {
            long days= ChronoUnit.DAYS.between(start,end);
            return days*price;
        }catch (Exception e){
            e.getMessage();
            return 0;
        }

    }

    public static boolean makeReservation(CreateReservationDto reservationDto){
        return ReservationRepository.reserve(reservationDto);
    }

    public static ObservableList<Reservation> listReservationRooms() {
        return ReservationRepository.listUserReservations();
    }

    public static ObservableList<Reservation> listHistoryRooms() {
        return ReservationRepository.listUserReservationHistory();
    }

    public static int updateUserReservations(String email){return ReservationRepository.getReservationCountForUser(email);}
}
