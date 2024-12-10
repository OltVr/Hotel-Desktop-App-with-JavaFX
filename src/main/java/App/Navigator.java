package App;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Navigator {
    public final static String LOGIN_PAGE = "login_form.fxml";
    public final static String HOME_PAGE = "user_pages.fxml";
    public final static String CREATE_ACCOUNT_PAGE = "signup_form.fxml";
    public final static String ADMIN_DASHBOARD = "admin_pages.fxml";
    public final static String PROCEEDING_PAGE = "reserve_form.fxml";
    private static ResourceBundle bundle;
    public static Locale locale = Locale.getDefault();
    private static String currentPage;


    private static String currentVisibleSection;
    private static String previousPage;


    public static void navigate(Stage stage, String page) {
        FXMLLoader loader = new FXMLLoader(
                Navigator.class.getResource(page)
        );


        if (locale.getLanguage().equals("sq")) {
            bundle = ResourceBundle.getBundle("translations.content_sq"); // Load Albanian resource bundle
        } else {
            bundle = ResourceBundle.getBundle("translations.content_en"); // Load English resource bundle by default
        }

        loader.setResources(bundle);
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            currentPage = page;
        } catch (IOException ioe) {
            System.out.println("[DEBUG]" + ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public static void navigate(Event event, String page) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        navigate(stage, page);
    }


    public static void changeLanguage(Event e, String localeCode) {
        locale = new Locale(localeCode);
        bundle = ResourceBundle.getBundle("translations.content", locale);
        updateUI(e);
    }

    private static void updateUI(Event event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(currentPage));
            loader.setResources(bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();



            if (currentVisibleSection != null) {
                Pane dashboardPane = (Pane) root.lookup("#dashboardPane");
                Pane reservationPane = (Pane) root.lookup("#reservationPane");
                Pane roomPane = (Pane) root.lookup("#roomPane");
                Pane guestsPane = (Pane) root.lookup("#guestsPane");
                Pane homePane = (Pane) root.lookup("#homePane");
                Pane historyPane = (Pane) root.lookup("#historyPane");
                Pane reservationsPane = (Pane) root.lookup("#reservationsPane");
                Pane cityViewPane = (Pane) root.lookup("#cityViewPane");
                Pane seaViewPane = (Pane) root.lookup("#seaViewPane");
                Pane addStaffPane = (Pane) root.lookup("#addStaffPane");
                Pane staffListPane = (Pane) root.lookup("#staffListPane");


                if (dashboardPane != null) {
                    dashboardPane.setVisible(false);
                }
                if (reservationPane != null) {
                    reservationPane.setVisible(false);
                }
                if (roomPane != null) {
                    roomPane.setVisible(false);
                }
                if (guestsPane != null) {
                    guestsPane.setVisible(false);
                }
                if (homePane != null) {
                    homePane.setVisible(false);
                }
                if (historyPane != null) {
                    historyPane.setVisible(false);
                }
                if (reservationsPane != null) {
                    reservationsPane.setVisible(false);
                }
                if (cityViewPane != null) {
                    cityViewPane.setVisible(false);
                }
                if (seaViewPane != null) {
                    seaViewPane.setVisible(false);
                }
                if (addStaffPane != null) {
                    addStaffPane.setVisible(false);
                }  if (staffListPane != null) {
                    staffListPane.setVisible(false);
                }

                Node section = root.lookup(currentVisibleSection);
                if (section != null) {
                    section.setVisible(true);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void setCurrentVisibleSection(String sectionId) {
        currentVisibleSection = sectionId;
    }

}