package controllers;

import App.Navigator;
import database.DatabaseUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.Room;
import model.Staff;
import model.User;
import model.dto.InsertRoomDto;
import model.dto.InsertStaffDto;
import model.Reservation;
import model.filter.ReservationFilter;
import model.filter.StaffFilter;
import model.filter.UserFilter;
import service.AdminService;
import javafx.scene.control.Alert;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private static String pane;

    @FXML
    private AnchorPane dashboardPane;
    @FXML
    private AnchorPane roomPane;
    @FXML
    private AnchorPane reservationPane;
    @FXML
    private AnchorPane guestsPane;
    @FXML
    private AnchorPane addStaffPane;
    @FXML
    private AnchorPane staffListPane;

    // Rooms Management Page
    @FXML
    private TextField txtRoom;
    @FXML
    private TextField txtFloor;
    @FXML
    private TextField txtPrice;
    @FXML
    private ComboBox<String> roomType;
    @FXML
    private ComboBox<Integer> Capacity;
    @FXML
    private ComboBox<Integer> Beds;

    // Tabela Rooms
    @FXML
    private TableView<Room> roomTable;
    @FXML
    private TableColumn<Room, String> roomNumber_col;
    @FXML
    private TableColumn<Room, String> floorNumber_col;
    @FXML
    private TableColumn<Room, String> roomType_col;

    @FXML
    private TableColumn<Room, String> price_col;
    @FXML
    private TableColumn<Room, String> capacity_col;
    @FXML
    private TableColumn<Room, String> bedNumber_col;
    @FXML
    private TableColumn<Room, String> Available_col;

    //Tabela User
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> email_col;
    @FXML
    private TableColumn<User, String> firstName_col;
    @FXML
    private TableColumn<User, String> lastName_col;
    @FXML
    private TableColumn<User, String> isAdmin_col;
    @FXML
    private TableColumn<User, Timestamp> CreatedAt_col;

    // Admin Dashboard Page
    @FXML
    private Text txtNewUsers;
    @FXML
    private Text txtRoomsBooked;
    @FXML
    private Text txtTotalIncome;

    // Tabela Reservations
    @FXML
    private TableView<Reservation> reservationTable;
    @FXML
    private TableColumn<Reservation, Integer> Res_reservationID_col;
    @FXML
    private TableColumn<Reservation, String> Res_email_col;
    @FXML
    private TableColumn<Reservation, Integer> Res_roomNumber_col;
    @FXML
    private TableColumn<Reservation, Date> Res_reservationDate_col;
    @FXML
    private TableColumn<Reservation, Date> Res_checkInDate_col;
    @FXML
    private TableColumn<Reservation, Date> Res_checkOutDate_col;
    @FXML
    private TableColumn<Reservation, Integer> Res_totalPrice_col;

    // chart
    @FXML
    private TextField searchField;
    @FXML
    private LineChart<String, Number> chartDashboard;
    @FXML
    private TextField searchFieldUser;
    @FXML
    private TextField searchFieldStaff;

    //Staff

    @FXML
    private TextField txtStaffFirstName;
    @FXML
    private TextField txtStaffLastName;
    @FXML
    private TextField txtStaffEmail;
    @FXML
    private TextField txtStaffSalary;
    @FXML
    private RadioButton radioManager;
    @FXML
    private RadioButton radioWaiter;
    @FXML
    private RadioButton radioReceptionist;
    @FXML
    private RadioButton radioTech;
    @FXML
    private CheckBox checkFullTime;
    @FXML
    private CheckBox checkBenefits;

    //Tabela Staff
    @FXML
    private TableView<Staff> reservationTable1;
    @FXML
    private TableColumn<Staff, Integer> SID_col;
    @FXML
    private TableColumn<Staff, String> Name_col;
    @FXML
    private TableColumn<Staff, String> staffEmail_col;
    @FXML
    private TableColumn<Staff, String> position_col;
    @FXML
    private TableColumn<Staff, Double> salary_col;
    @FXML
    private TableColumn<Staff, Boolean> FullTime_col;
    @FXML
    private TableColumn<Staff, Boolean> Benefits_col;

    private String anchorPane = "Dashboard";

    @FXML
    private void handleLogout(MouseEvent me) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to Sign Out?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            Navigator.navigate(me, Navigator.LOGIN_PAGE);
        }
    }


    // ADMIN DASHBOARD
    private void populateChart(){
        XYChart.Series<String, Number> series = AdminService.getDailyIncomeSeries();
        chartDashboard.getData().add(series);
    }

    private void updateNewUsers(){
        String newUsers=String.valueOf(AdminService.newUsers());
        txtNewUsers.setText(newUsers);
    }


    private void updateRoomsBooked(){
        String count= String.valueOf(AdminService.RoomsBooked());
        txtRoomsBooked.setText(count);

    }

    private void updateTotalIncome(){
        String total=String.valueOf(AdminService.TotalIncome()+" $");
        txtTotalIncome.setText(total);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Staff management
    @FXML
    private void handleAddStaff() {
        String firstName = txtStaffFirstName.getText();
        String lastName = txtStaffLastName.getText();
        String email = txtStaffEmail.getText();
        String salaryText = txtStaffSalary.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || salaryText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fill all staff information fields first.");
            alert.showAndWait();
            return;
        }
        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid salary.");
            alert.showAndWait();
            return;
        }

        String position = "";
        if (radioManager.isSelected()) {
            position = "General Manager";
        } else if (radioWaiter.isSelected()) {
            position = "Waiter";
        } else if (radioReceptionist.isSelected()) {
            position = "Receptionist";
        } else if (radioTech.isSelected()) {
            position = "Maintenance Technician";
        }

        boolean isFullTime = checkFullTime.isSelected();
        boolean hasBenefits = checkBenefits.isSelected();

        InsertStaffDto staffData = new InsertStaffDto(firstName, lastName, email, position, salary, isFullTime, hasBenefits);

        boolean isSuccess = AdminService.addStaff(staffData);

        if (isSuccess) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Staff was added successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add staff.");
            alert.showAndWait();
        }
        showStaffList();
    }

    private void showStaffList(){
        ObservableList<Staff> staffListData = AdminService.ListStaff();

        SID_col.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStaffID()).asObject());
        Name_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStaffFirstName() + " " + cellData.getValue().getStaffLastName()));
        staffEmail_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStaffEmail()));
        position_col.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPosition()));
        salary_col.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSalary()).asObject());
        FullTime_col.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isFullTime()).asObject());
        Benefits_col.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isHasBenefits()).asObject());

        FullTime_col.setCellFactory(column -> new TableCell<Staff, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Full Time" : "Half Time");
                }
            }
        });

        Benefits_col.setCellFactory(column -> new TableCell<Staff, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "YES" : "NO");
                }
            }
        });

        reservationTable1.setItems(staffListData);
    }

    @FXML
    private void handleDeleteStaff(){
        Staff selectedStaff = reservationTable1.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this staff member?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                boolean success = AdminService.deleteStaff(selectedStaff.getStaffEmail());
                if (success) {
                    reservationTable1.getItems().remove(selectedStaff);
                } else {
                    showAlert("Error", "Failed to delete staff member.");
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No user selected.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearchStaff() {
        String searchTerm = searchFieldStaff.getText().trim();
        if (!searchTerm.isEmpty()) {
            StaffFilter staffFilter = new StaffFilter(searchTerm);
            String query = staffFilter.buildQuery();
            ObservableList<Staff> searchResults = AdminService.searchStaff(query);
            if (!searchResults.isEmpty()) {
                reservationTable1.setItems(searchResults);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No staff member found for the given email address.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter an email address.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleStaff() {
        setAllPanesInvisible();
        addStaffPane.setVisible(true);
        pane = "addStaff";
        Navigator.setCurrentVisibleSection("#addStaffPane");
    }

    @FXML
    private void handleStaffList() {
        setAllPanesInvisible();
        staffListPane.setVisible(true);
        pane = "staffList";
        Navigator.setCurrentVisibleSection("#staffListPane");
    }

    // ROOM MANAGEMENT
    @FXML
    private void handleAddRoom() {
        String roomText = this.txtRoom.getText();
        String floorText = this.txtFloor.getText();
        String roomType = this.roomType.getSelectionModel().getSelectedItem();
        Integer capacity = this.Capacity.getSelectionModel().getSelectedItem();
        Integer beds = this.Beds.getSelectionModel().getSelectedItem();
        String priceText = this.txtPrice.getText();


        if (roomText.isEmpty() || floorText.isEmpty() || roomType == null || capacity == null || beds == null || priceText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fill all room information fields first.");
            alert.showAndWait();
            return;
        }

        int roomNumber;
        int floorNumber;
        double price;
        try {
            roomNumber = Integer.parseInt(roomText);
            floorNumber = Integer.parseInt(floorText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for room number, floor, and price.");
            alert.showAndWait();
            return;
        }

        InsertRoomDto roomData = new InsertRoomDto(roomNumber, floorNumber, roomType, capacity, beds, price);

        boolean isSuccess = AdminService.addRoom(roomData);
        if (isSuccess) {
            showRoomList();
            clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Room was added successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add room. Check if this room already exists.");
            alert.showAndWait();
        }
    }

    private void showRoomList(){
        ObservableList<Room> listData = AdminService.ListRoom();

        roomNumber_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        floorNumber_col.setCellValueFactory(new PropertyValueFactory<>("floorNumber"));
        roomType_col.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        capacity_col.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        bedNumber_col.setCellValueFactory(new PropertyValueFactory<>("bedNumber"));
        price_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        Available_col.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();
            String availability = room.isAvailable() ? "Available" : "Unavailable";
            return new SimpleStringProperty(availability);
        });

        roomTable.setItems(listData);
    }

    @FXML
    private void handleDeleteRoom() {
        String roomText = txtRoom.getText();
        String floorText = txtFloor.getText();


        if (roomText.isEmpty() || floorText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in both the room number and floor number.");
            alert.showAndWait();
            return;
        }


        int roomNumber;
        int floorNumber;
        try {
            roomNumber = Integer.parseInt(roomText);
            floorNumber = Integer.parseInt(floorText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for room number and floor number.");
            alert.showAndWait();
            return;
        }

        if (AdminService.deleteRoom(roomNumber, floorNumber)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this room?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                showRoomList();
                clear();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Couldn't find room.");
            alert.showAndWait();
        }
    }

    @FXML
    private void  roomSelect(){
        Room room = roomTable.getSelectionModel().getSelectedItem();


        if (room!=null){
            txtRoom.setText(String.valueOf(room.getRoomNumber()));
            txtFloor.setText(String.valueOf(room.getFloorNumber()));
            txtPrice.setText(String.valueOf(room.getPrice()));}

    }

    @FXML
    private void handleRooms() {
        setAllPanesInvisible();
        roomPane.setVisible(true);
        pane = "Rooms";
        Navigator.setCurrentVisibleSection("#roomPane");
    }

    @FXML
    private void handleUpdate() {
        String roomText = this.txtRoom.getText();
        String floorText = this.txtFloor.getText();
        String roomType = this.roomType.getSelectionModel().getSelectedItem();
        Integer capacity = this.Capacity.getSelectionModel().getSelectedItem();
        Integer beds = this.Beds.getSelectionModel().getSelectedItem();
        String priceText = this.txtPrice.getText();

        if (roomText.isEmpty() || floorText.isEmpty() || roomType == null || capacity == null || beds == null || priceText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fill all room information fields first.");
            alert.showAndWait();
            return;
        }

        int roomNumber;
        int floorNumber;
        double price;
        try {
            roomNumber = Integer.parseInt(roomText);
            floorNumber = Integer.parseInt(floorText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter valid numeric values for room number, floor, and price.");
            alert.showAndWait();
            return;
        }

        InsertRoomDto roomData = new InsertRoomDto(roomNumber, floorNumber, roomType, capacity, beds, price);

        if (AdminService.updateRoom(roomData)) {
            showRoomList();
            clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Room was updated successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The room either does not exist or there was a database error.");
            alert.showAndWait();
        }
    }

    // Nderrimi i faqeve me visibility prej scenebuilder
    private void setAllPanesInvisible() {
        dashboardPane.setVisible(false);
        reservationPane.setVisible(false);
        roomPane.setVisible(false);
        guestsPane.setVisible(false);
        staffListPane.setVisible(false);
        addStaffPane.setVisible(false);
    }
    @FXML
    private void handleDashboard() {
        setAllPanesInvisible();
        dashboardPane.setVisible(true);
        pane = "Dashboard";
        Navigator.setCurrentVisibleSection("#dashboardPane");
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
    private void handleGuests() {
        setAllPanesInvisible();
        guestsPane.setVisible(true);
        pane = "Guests";
        Navigator.setCurrentVisibleSection("#guestsPane");
    }

    @FXML
    private void handleBack() {
        setAllPanesInvisible();
        addStaffPane.setVisible(true);
        pane = "addStaff";
        Navigator.setCurrentVisibleSection("#addStaffPane");
    }

    //Reservation management
    private void showReservationList() {
        ObservableList<Reservation> listData = AdminService.ListReservations();

        Res_reservationID_col.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        Res_email_col.setCellValueFactory(new PropertyValueFactory<>("email"));
        Res_roomNumber_col.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        Res_reservationDate_col.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        Res_checkInDate_col.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        Res_checkOutDate_col.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        Res_totalPrice_col.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        reservationTable.setItems(listData);
    }
    @FXML
    private void handleDeleteReservation() {
        Reservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this reservation?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                boolean success = AdminService.deleteReservation(selectedReservation.getReservationID());
                if (success) {
                    reservationTable.getItems().remove(selectedReservation);
                } else {
                    showAlert("Error", "Failed to delete reservation");
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No reservation selected.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleSearchReservation() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                Integer reservationID = Integer.parseInt(searchTerm);
                ReservationFilter reservationFilter = new ReservationFilter(null, reservationID);
                String query = reservationFilter.buildQuery();
                ObservableList<Reservation> searchResults = AdminService.searchReservations(query, reservationFilter);
                if (!searchResults.isEmpty()) {
                    reservationTable.setItems(searchResults);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No reservations found for the given ID");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                ReservationFilter reservationFilter = new ReservationFilter(searchTerm, null);
                String query = reservationFilter.buildQuery();
                ObservableList<Reservation> searchResults = AdminService.searchReservations(query, reservationFilter);
                if (!searchResults.isEmpty()) {
                    reservationTable.setItems(searchResults);
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "No reservations found for the given email");
                    alert.showAndWait();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a reservation ID or email.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleReservations() {
        setAllPanesInvisible();
        reservationPane.setVisible(true);
        pane = "Reservations";
        Navigator.setCurrentVisibleSection("#reservationPane");
    }

    //User management
    @FXML
    private void handleSearchUser() {
        String searchTerm = searchFieldUser.getText().trim();
        if (!searchTerm.isEmpty()) {
            UserFilter userFilter = new UserFilter(searchTerm);
            String query = userFilter.buildQuery();
            ObservableList<User> searchResults = AdminService.searchUsers(query);
            if (!searchResults.isEmpty()) {
                userTable.setItems(searchResults);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No users found for the given email address.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter an email address.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                boolean success = AdminService.deleteUser(selectedUser.getEmail());
                if (success) {
                    userTable.getItems().remove(selectedUser);
                } else {
                    showAlert("Error", "Failed to delete user.");
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No user selected.");
            alert.showAndWait();
        }
    }

    private void showUserList(){
        ObservableList<User> listData = AdminService.ListUser();

        email_col.setCellValueFactory(new PropertyValueFactory<>("email"));
        firstName_col.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName_col.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        isAdmin_col.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String role = user.isAdmin() ? "Admin" : "User";
            return new SimpleStringProperty(role);
        });
        CreatedAt_col.setCellValueFactory(new PropertyValueFactory<>("CreatedAt"));

        userTable.setItems(listData);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roomType.getItems().addAll("Sea View", "City View");
        Capacity.getItems().addAll(1, 2,3,4,5,6,7,8);
        Beds.getItems().addAll(1, 2,3,4);
        showRoomList();
        showUserList();
        updateRoomsBooked();
        updateTotalIncome();
        updateNewUsers();
        showReservationList();
        populateChart();
        showStaffList();
        ToggleGroup positions = new ToggleGroup();

        radioManager.setToggleGroup(positions);
        radioWaiter.setToggleGroup(positions);
        radioReceptionist.setToggleGroup(positions);
        radioTech.setToggleGroup(positions);
    }

    private void clear(){
        txtRoom.setText("");
        txtFloor.setText("");
        txtPrice.setText("");
        roomType.getSelectionModel().clearSelection();
        roomType.setPromptText("Type");
        Capacity.getSelectionModel().clearSelection();
        Capacity.setPromptText("Capacity");
        Beds.getSelectionModel().clearSelection();
        Beds.setPromptText("Number of Beds");
    }

    @FXML
    private void handleChangeLanguage(ActionEvent ae){

        if (Navigator.locale.getLanguage().equals("en")){
            Navigator.changeLanguage(ae,"sq");
        }
        else if  (Navigator.locale.getLanguage().equals("sq")){
            Navigator.changeLanguage(ae,"en");
        }
    }
}
