package controllers;

import App.Navigator;
import App.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.Reservation;
import model.dto.CreateReservationDto;
import service.ReserveService;
import service.UserService;

import java.time.LocalDate;
import java.util.*;

public class ReserveController {
    public Button btnReserve;
    private ResourceBundle bundle;
    @FXML
    private Text rsrvRoom;
    @FXML
    private Text lblTotalPrice;
    @FXML
    private DatePicker checkInDate;
    @FXML
    private DatePicker checkOutDate;


    @FXML
    private void initialize(){
        Locale locale = Locale.getDefault();
        List<Reservation> reservations = ReserveService.getReservationsForRoom(SessionManager.getSelectedRoomNumber());
        if (locale.getLanguage().equals("sq")){
            bundle= ResourceBundle.getBundle("translations.content", locale);
        }
        else {
            bundle = ResourceBundle.getBundle("translations.content_en", locale);

        }
        rsrvRoom.setText(String.valueOf(SessionManager.getSelectedRoomNumber()));
        lblTotalPrice.setText("0.00");
        checkInDate.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                if(date.isBefore(LocalDate.now())|| isDateUnavailable(date, reservations)){
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
        checkOutDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty){
                super.updateItem(date, empty);
                if ((date.isBefore(checkInDate.getValue()) || date.isEqual(checkInDate.getValue()))|| isDateUnavailable(date, reservations)){
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb");
                }
            }
        });

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
    private void handleChangeLanguage(ActionEvent ae) {

        if (Navigator.locale.getLanguage().equals("en")){
            Navigator.changeLanguage(ae,"sq");
        }
        else if  (Navigator.locale.getLanguage().equals("sq")){
            Navigator.changeLanguage(ae,"en");
        }

    }


    @FXML
    private void resetCheckOutDate(){
        try {
            LocalDate date = checkOutDate.getValue();
            if (date.isEqual(checkInDate.getValue()) || date.isBefore(checkInDate.getValue())) {
                checkOutDate.setValue(null);
                lblTotalPrice.setText("0.00");
            }
            else {
                double totalprice = UserService.totalPrice(checkInDate.getValue(), date, SessionManager.getPrice());
                lblTotalPrice.setText(String.valueOf(totalprice));
            }
        }catch (NullPointerException ne){
            ne.getMessage();
        }
    }
    @FXML
    private void getPrices(){
        LocalDate startDate = checkInDate.getValue();
        LocalDate endDate = checkOutDate.getValue();
        if (checkOutDate!=null) {
            double totalprice = UserService.totalPrice(startDate, endDate, SessionManager.getPrice());
            lblTotalPrice.setText(String.valueOf(totalprice));
        }
        else if(endDate.isBefore(startDate)){
            lblTotalPrice.setText("0.00");
        }
    }

    @FXML
    private void handleReserve(ActionEvent ae){
        CreateReservationDto reservation= new CreateReservationDto(
                SessionManager.getUserEmail(),
                SessionManager.getSelectedRoomNumber(),
                checkInDate.getValue(),
                checkOutDate.getValue()
        );
        if(checkOutDate.getValue()==null){
            Alert alert=new Alert(Alert.AlertType.WARNING,"Please select a check out date first!",ButtonType.OK);
            alert.show();
        }
        if(!UserService.makeReservation(reservation)){
            Alert alert=new Alert(Alert.AlertType.ERROR,"Reservation failed, please try again!",ButtonType.OK);
            Optional<ButtonType> rez=alert.showAndWait();
            if (rez.get() == ButtonType.OK){
                alert.close();
            }
        }
        else {
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Reservation is made successfully, thanks for choosing us. ",ButtonType.OK);
            Optional<ButtonType> rez=alert.showAndWait();
            if (rez.get() == ButtonType.OK){
                alert.close();
                Navigator.navigate(ae,Navigator.HOME_PAGE);
            }

        }
    }


    public void handleProceeding(MouseEvent me) {
        Navigator.navigate(me, Navigator.PROCEEDING_PAGE);
    }

    @FXML
    private void handleBack(ActionEvent ae){
        Navigator.navigate(ae, Navigator.HOME_PAGE);
    }
    private boolean isDateUnavailable(LocalDate date, List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            if ((date.isEqual(reservation.getCheckInDate().toLocalDate()) || date.isAfter(reservation.getCheckInDate().toLocalDate())) &&
                    date.isBefore(reservation.getCheckOutDate().toLocalDate())) {
                return true;
            }
        }
        return false;
    }
}