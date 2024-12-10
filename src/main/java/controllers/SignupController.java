package controllers;

import App.Navigator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import model.dto.UserDto;
import service.UserService;

import java.util.Locale;
import java.util.ResourceBundle;

public class SignupController {
    @FXML
    private BorderPane root;
    @FXML
    private TextField txtSignUpName;
    @FXML
    private TextField txtSignUpLastName;
    @FXML
    private TextField txtSignUpEmail;
    @FXML
    private PasswordField pwdSignUpPassword;
    @FXML
    private PasswordField pwdSignUpConfirmPassword;
    @FXML
    private Label statusBar;


    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^./&*?+=!])(?=\\S+$).{8,}$";

    private ResourceBundle bundle;

    @FXML
    private void initialize(){
        Locale locale = Locale.getDefault();

        if (locale.getLanguage().equals("sq")){
            bundle= ResourceBundle.getBundle("translations.content", locale);
        }
        else {
            bundle = ResourceBundle.getBundle("translations.content_en", locale);
        }
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
    private void handleSignUp(ActionEvent ae) {
        String email = txtSignUpEmail.getText();
        String password = pwdSignUpPassword.getText();
        String confirmPassword = pwdSignUpConfirmPassword.getText();


        if (!email.matches(EMAIL_REGEX)) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }


        if (UserService.userExists(email)) {
            showAlert("Email Already Exists", "The email address is already registered. Please use a different email.");
            return;
        }


        if (!password.matches(PASSWORD_REGEX)) {
            showAlert("Invalid Password", "Password requires at least 1 digit, lowercase & uppercase letter, special character, and minimum length of 8 characters.");
            return;
        }


        if(!password.matches(confirmPassword)){
            showAlert("Sign Up Failed! ", "Passwords do not match.");
            return;
        }



        UserDto userSignUpData = new UserDto(
                this.txtSignUpName.getText(),
                this.txtSignUpLastName.getText(),
                email,
                password,
                this.pwdSignUpConfirmPassword.getText()
        );

        boolean response = UserService.signUp(userSignUpData);

        if (response) {
            statusBar.setVisible(true);
            root.setBottom(statusBar);
            BorderPane.setAlignment(statusBar, Pos.CENTER);

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                statusBar.setVisible(false);

                Navigator.navigate(ae, Navigator.LOGIN_PAGE);
            }));
            timeline.play();
        }
    }

    @FXML
    private void handleHaveAnAccount(MouseEvent me){
        Navigator.navigate(me, Navigator.LOGIN_PAGE);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
