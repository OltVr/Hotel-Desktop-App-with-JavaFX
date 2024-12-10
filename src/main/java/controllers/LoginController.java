package controllers;

import App.Navigator;
import App.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.User;
import model.dto.LoginUserDto;
import service.UserService;

public class LoginController {
    @FXML
    private TextField txtLoginEmail;
    @FXML
    private PasswordField pwdLoginPassword;

    @FXML
    public void initialize() {
        // Set up event listeners for the text fields
        txtLoginEmail.setOnKeyPressed(this::handleKeyLogin);
        pwdLoginPassword.setOnKeyPressed(this::handleKeyLogin);
    }
    @FXML
    private AnchorPane login_failed;
    @FXML
    private void handleLogin(ActionEvent ae){
        LoginUserDto loginUserData = new LoginUserDto(
                this.txtLoginEmail.getText(),
                this.pwdLoginPassword.getText()
        );
        boolean isLogin= UserService.login(loginUserData);

        if (!isLogin){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Login Failed! Check your credentials and try again.");
            alert.show();
        }
        else{
            boolean Admin=UserService.loginAdmin(loginUserData);
            if (!Admin){
                User user= UserService.getUserEmail(loginUserData.getEmail());
                if(user != null){
                    SessionManager.setUser(user);
                    Navigator.navigate(ae,Navigator.HOME_PAGE);
                }

            }
            else{
                Navigator.navigate(ae,Navigator.ADMIN_DASHBOARD);
            }

        }
    }
    @FXML
    private void handleKeyLogin(KeyEvent ke) {
        if (ke.getCode() == KeyCode.ENTER) {
            LoginUserDto loginUserData = new LoginUserDto(
                    this.txtLoginEmail.getText(),
                    this.pwdLoginPassword.getText()
            );
            boolean isLogin= UserService.login(loginUserData);

            if (!isLogin){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Login Failed! Check your credentials and try again.");
                alert.show();

            }
            else{
                boolean Admin=UserService.loginAdmin(loginUserData);
                if (!Admin){
                    User user= UserService.getUserEmail(loginUserData.getEmail());
                    if(user != null){
                        SessionManager.setUser(user);
                        Navigator.navigate(ke,Navigator.HOME_PAGE);
                    }
                }
                else{
                    Navigator.navigate(ke,Navigator.ADMIN_DASHBOARD);
                }

            }
        }
    }
        @FXML
        private void handleCreateAccount (MouseEvent me){
            Navigator.navigate(me, Navigator.CREATE_ACCOUNT_PAGE);
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
    private void handleOkClick(ActionEvent ae) {
        Navigator.navigate(ae, Navigator.LOGIN_PAGE);
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
}
