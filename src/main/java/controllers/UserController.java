package controllers;

import App.Navigator;
import App.SessionManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.Reservation;
import model.Room;
import service.UserService;

import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;


public class UserController implements Initializable {
    @FXML
    private AnchorPane homePane;
    @FXML
    private AnchorPane reservationsPane;
    @FXML
    private AnchorPane historyPane;
    @FXML
    private AnchorPane seaViewPane;
    @FXML
    private AnchorPane cityViewPane;


    @FXML
    private TableView<Room> seaTable;
    @FXML
    private TableColumn<Room, Integer> seaViewRoomNumber_col;
    @FXML
    private TableColumn<Room, Integer> seaViewFloorNumber_col;
    @FXML
    private TableColumn<Room, Integer> seaViewBedNumber_col;
    @FXML
    private TableColumn<Room, Double> seaViewPrice_col;
    @FXML
    private TableColumn<Room, Integer> seaViewCapacity_col;


    @FXML
    private TableView<Room> cityTable;
    @FXML
    private TableColumn<Room, Integer> cityViewRoomNumber_col;
    @FXML
    private TableColumn<Room, Integer> cityViewFloorNumber_col;
    @FXML
    private TableColumn<Room, Integer> cityViewBedNumber_col;
    @FXML
    private TableColumn<Room, Double> cityViewPrice_col;
    @FXML
    private TableColumn<Room, Integer> cityViewCapacity_col;
    @FXML
    private Text txtfirstName;
    private String anchorPane = "Home";

    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Room, Text> reservationRoom_col;
    @FXML
    private TableColumn<Room, Date> reservationDate_col;
    @FXML
    private TableColumn<Room, Text> reservationPrice_col;
    @FXML
    private TableColumn<Room, DatePicker> reservationCheckIn_col;
    @FXML
    private TableColumn<Room, DatePicker> reservationCheckOut_col;


    @FXML
    private TableView<Reservation> historyTable;
    @FXML
    private TableColumn<Room, Text> historyRoom_col;
    @FXML
    private TableColumn<Room, Date> historyDate_col;
    @FXML
    private TableColumn<Room, Text> historyPrice_col;
    @FXML
    private TableColumn<Room, DatePicker> historyCheckIn_col;
    @FXML
    private TableColumn<Room, DatePicker> historyCheckOut_col;

    @FXML
    private Text txtUserReservations;


    public void showUserReservations(){
        String email=SessionManager.getUserEmail();
        if (email != null){
            txtUserReservations.setText(String.valueOf(UserService.updateUserReservations(SessionManager.getUserEmail())));
        }
    }

    public void showFirstName(){
        String emri= SessionManager.getUserName();
        if (emri != null) {
            txtfirstName.setText(emri);
        }else{
            System.out.println("Firstname is null !!!");
        }
    }

    private void setAllPanesInvisible() {
        homePane.setVisible(false);
        reservationsPane.setVisible(false);
        cityViewPane.setVisible(false);
        seaViewPane.setVisible(false);
        historyPane.setVisible(false);
    }

    @FXML
    private void handleHome(){
        setAllPanesInvisible();
        homePane.setVisible(true);
        anchorPane = "Home";
        Navigator.setCurrentVisibleSection("#homePane");
    }
    @FXML
    private void handleReservations(){
        setAllPanesInvisible();
        reservationsPane.setVisible(true);
        anchorPane = "Reservations";
        Navigator.setCurrentVisibleSection("#reservationsPane");
    }
    @FXML
    private void handleHistory(){
        setAllPanesInvisible();
        historyPane.setVisible(true);
        anchorPane = "History";
        Navigator.setCurrentVisibleSection("#historyPane");
    }
    @FXML
    private void handleHelp(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contact Us");
        alert.setHeaderText(null);
        alert.setContentText("For support, please contact us:\n\nEmail: support@royalrest.com\nPhone: +383 45 117 755");
        alert.showAndWait();
    }

    @FXML
    private void handleLogOut(ActionEvent ae) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to Log Out?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Navigator.navigate(ae, Navigator.LOGIN_PAGE);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showSeaViewRooms();
        showCityViewRooms();
        showFirstName();
        showReservationRooms();
        showHistoryRooms();
        showUserReservations();
    }

    @FXML
    public void handleCheckSeaView(MouseEvent me){
        setAllPanesInvisible();
        seaViewPane.setVisible(true);
        anchorPane = "SeaView";
        Navigator.setCurrentVisibleSection("#seaViewPane");
    }
    @FXML
    public void handleCheckCityView(MouseEvent me){
        setAllPanesInvisible();
        cityViewPane.setVisible(true);
        anchorPane = "CityView";
        Navigator.setCurrentVisibleSection("#cityViewPane");
    }
    @FXML
    public void handleProceeding(MouseEvent me) {
        Room selectedRoom = seaTable.getSelectionModel().getSelectedItem();
        Room selectedRoom1= cityTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null || selectedRoom1!=null) {
            Navigator.navigate(me, Navigator.PROCEEDING_PAGE);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Room Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a room to proceed.");
            alert.showAndWait();
        }
    }

    private void showSeaViewRooms() {
        ObservableList<Room> listData = UserService.listSeaViewRooms();

        seaViewRoomNumber_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        seaViewFloorNumber_col.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        seaViewBedNumber_col.setCellValueFactory(new PropertyValueFactory<>("bedNumber"));
        seaViewPrice_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        seaViewCapacity_col.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        seaTable.setItems(listData);
    }

    private void showCityViewRooms() {
        ObservableList<Room> listData = UserService.listCityViewRooms();

        cityViewRoomNumber_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        cityViewFloorNumber_col.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        cityViewBedNumber_col.setCellValueFactory(new PropertyValueFactory<>("bedNumber"));
        cityViewPrice_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        cityViewCapacity_col.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        cityTable.setItems(listData);
    }


    @FXML
    private void handleChangeLanguage(ActionEvent ae) {

        if (Navigator.locale.getLanguage().equals("en")){
            Navigator.changeLanguage(ae,"sq");

        }
        else if  (Navigator.locale.getLanguage().equals("sq")){
            Navigator.changeLanguage(ae,"en");
        }

    }
    @FXML
    private void  seaRoomSelect() {
        Room room = seaTable.getSelectionModel().getSelectedItem();


        if (room != null) {
            SessionManager.setRoom(room);
        }
    }
    @FXML
    private void  cityRoomSelect() {
        Room room = cityTable.getSelectionModel().getSelectedItem();


        if (room != null) {
            SessionManager.setRoom(room);
        }
    }


    @FXML
    public void handleCancelReservation(){
       Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this reservation?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                boolean success = UserService.cancelReservation(selectedReservation.getReservationID());
                if (success) {
                    reservationTable.getItems().remove(selectedReservation);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No reservation selected!");
            alert.showAndWait();
        }
    }

    private void showReservationRooms() {
        ObservableList<Reservation> listData = UserService.listReservationRooms();

        reservationRoom_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        reservationDate_col.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        reservationPrice_col.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        reservationCheckIn_col.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        reservationCheckOut_col.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        reservationTable.setItems(listData);
    }

    private void showHistoryRooms() {
        ObservableList<Reservation> listData = UserService.listHistoryRooms();

        historyRoom_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        historyDate_col.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        historyPrice_col.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        historyCheckIn_col.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        historyCheckOut_col.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        historyTable.setItems(listData);
    }

}
