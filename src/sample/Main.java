package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import novj.publ.api.NovaOpt;

public class Main extends Application {

    /**
     * 跳转到程序首页
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            initValue();
            URL location = getClass().getResource("MainPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();
            primaryStage.setTitle("SdkDemo");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            NovaOpt.GetInstance().initialize(2);
            MainPageController controller = fxmlLoader.getController();
            controller.initData();
            //region    查询设备
            controller.searchScreen();
            //endregion
        } catch (Exception ex) {
            System.out.println(ex);
            showInfo(ex.getMessage());
        }
    }

    public void initValue() {
        try {
            String curPath = System.getProperty("user.dir");
            Contacts.timeZonesXml = curPath + File.separator + "timeZones.xml";
            File config = new File(System.getProperty("user.dir") + File.separator +
                    "configuration.properties");
            if (config.exists()) {
                FileReader fr = new FileReader(config);
                BufferedReader br = new BufferedReader(fr);
                Properties properties = new Properties();
                properties.load(br);
                if (Locale.ENGLISH.getLanguage().equals(properties.getProperty("local_language"))) {
                    Contacts.DEFAULT_LOCALE = Locale.ENGLISH;
                } else if (Locale.CHINESE.getLanguage().equals(properties.getProperty
                        ("local_language"))) {
                    Contacts.DEFAULT_LOCALE = Locale.CHINESE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showInfo(String sInfo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, sInfo);
        alert.setHeaderText(null);
        alert.setTitle(Contacts.getResourceValue("alert_title_confirm"));
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
 System.out.println("hello");
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
