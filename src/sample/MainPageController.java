package sample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import beans.DeviceInfo;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import novj.platform.vxkit.common.bean.search.SearchResult;
import novj.platform.vxkit.common.result.DefaultOnResultListener;
import novj.platform.vxkit.handy.api.SearchManager;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import sample.pages.LoginPageController;
import uiexdata.ViewModel;

/**
 * @author
 */
public class MainPageController {
    @FXML
    private TableView<DeviceInfo> deviceTable;
    @FXML
    private TableColumn<DeviceInfo, String> snColumn;
    @FXML
    private TableColumn<DeviceInfo, String> deviceNameColumn;
    @FXML
    private TableColumn<DeviceInfo, String> aliasNameColumn;
    @FXML
    private TableColumn<DeviceInfo, String> ipAddressColumn;
    @FXML
    private TableColumn<DeviceInfo, Button> connectBtnColumn;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelUnlogin;
    @FXML
    private Button btn_refresh;
    @FXML
    private ComboBox cb_select_language;
    //    private Map<String, String> loginStatus = new HashMap<>();
    @FXML
    private ListView listview;
    @FXML
    private Button btn_logout;
    private DeviceInfo selectedDeviceInfo;


    /**
     * search device
     */
    public void searchScreen() {
        System.out.println("insile Search ");
        searchScreen("192.168.0.182");
    }

    private void searchScreen(String remoteIp) {
        btn_logout.setDisable(true);
        ViewModel.devicesData.clear();
        setLabelTotal();
        setLabelUnlogin();
        System.out.println("insile Search12 ");

        NovaOpt.GetInstance().searchScreen(new SearchManager.OnScreenSearchListener() {
            @Override
            public void onSuccess(SearchResult searchResult) {
                System.out.println(searchResult+"result");
                DeviceInfo deviceInfo = new DeviceInfo();

                deviceInfo.setSn(searchResult.sn);
                deviceInfo.setProductName(searchResult.productName);
                deviceInfo.setAliasName(searchResult.aliasName);
                deviceInfo.setIpAddress(searchResult.ipAddress);
                deviceInfo.setLogined(searchResult.logined);
                deviceInfo.searchResult = searchResult;

                add2list(deviceInfo);
            }

            @Override
            public void onError(ErrorDetail errorDetail) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, errorDetail.description);
                errorAlert.setTitle(Contacts.getResourceValue("alert_title_error"));
                errorAlert.setHeaderText(Contacts.getResourceValue("alert_title_error"));
                errorAlert.showAndWait();
            }
        }, remoteIp);
    }

    /**
     * 判断本地设备列表中是否已经包含指定终端，因为有多张网卡的机器默认会每张网卡都向终端发一个广播，
     * 所以同一台终端可能多次返回
     *
     * @param deviceInfo
     * @return
     */
    private boolean containsSN(DeviceInfo deviceInfo) {
        for (DeviceInfo item : ViewModel.devicesData) {
            if (item.getSn().equals(deviceInfo.getSn())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将返回的终端添加到本地列表中统一维护
     *
     * @param deviceInfo
     */
    private void add2list(DeviceInfo deviceInfo) {
        if (!containsSN(deviceInfo)) {
            ViewModel.devicesData.add(deviceInfo);
        }
        Platform.runLater(() -> {
            setLabelUnlogin();
            setLabelTotal();
        });
    }

    /**
     * 初始化Listview
     */
    private void initListView() {
        btn_logout.setDisable(true);
        listview.setItems(ViewModel.devicesData);
        listview.setCellFactory(e -> new ListCell<DeviceInfo>() {
            @Override
            protected void updateItem(DeviceInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (null != item && !empty) {
                    HBox hb = new HBox();
                    Label tfSn = new Label();
                    tfSn.setFont(Font.font(14));
                    tfSn.setPrefSize(250, 30);
                    tfSn.setText(item.getSn());

                    Label tfName = new Label();
                    tfName.setFont(Font.font(14));
                    tfName.setPrefSize(150, 30);
                    tfName.setText(item.getProductName());

                    Label tfIp = new Label();
                    tfIp.setFont(Font.font(14));
                    tfIp.setPrefSize(250, 30);
                    tfIp.setText(item.getIpAddress());

                    Button btn = new Button();
                    btn.setId(item.getSn());
                    btn.setText(item.isLogined() ?
                            Contacts.getResourceValue("terminal_login_direct") :
                            Contacts.getResourceValue("terminal_connect"));
                    btn.setOnMousePressed(event -> {
                        Button btn1 = (Button) event.getSource();
                        DeviceInfo info = ViewModel.getDeviceInfoBySN(btn1.getId());
                        System.out.println(info.getSn());
                        connectDevice(info);
                    });

                    Platform.runLater(() -> {
                        hb.getChildren().add(tfSn);
                        hb.getChildren().add(tfName);
                        hb.getChildren().add(tfIp);
                        hb.getChildren().add(btn);
                        hb.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(hb);
                    });
                } else {
                    Platform.runLater(() -> setGraphic(null));
                }
            }
        });
        listview.getFocusModel().focusedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                Number newValue) {
                if (newValue.intValue() >= 0) {
                    selectedDeviceInfo = ViewModel.devicesData.get(newValue.intValue());
                    if (selectedDeviceInfo.isLogined()) {
                        btn_logout.setDisable(false);
                    } else {
                        btn_logout.setDisable(true);
                    }
                } else {
                    selectedDeviceInfo = null;
                    btn_logout.setDisable(true);
                }
            }
        });
    }

    /**
     * 计算总设备数量
     */
    private void setLabelTotal() {
        labelTotal.setText(Contacts.getResourceValue("terminal_total") + ViewModel.devicesData.size());
    }

    /**
     * 计算未登录设备数量
     */
    private void setLabelUnlogin() {
        int totalUnLogined = 0;
        for (DeviceInfo deviceInfo : ViewModel.devicesData) {
            if (!deviceInfo.isLogined()) {
                totalUnLogined++;
            }
        }

        labelUnlogin.setText(Contacts.getResourceValue("terminal_not_logged") + (totalUnLogined >= 0 ? totalUnLogined : 0));
    }

    /**
     * 进入登录页面
     *
     * @param deviceInfo
     */
    private void toLoginPage(DeviceInfo deviceInfo) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("pages/LoginPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();
            LoginPageController loginPageController = fxmlLoader.getController();
            //传递参数
            loginPageController.setDeviceInfo(deviceInfo);
            loginPageController.mainPageController = this;
            if (deviceInfo.isLogined()) {
                Platform.runLater(() -> {
                    loginPageController.toDeviceOptPage(deviceInfo);
                });
            } else {
                primaryStage.initModality(Modality.APPLICATION_MODAL);
                primaryStage.setTitle(Contacts.getResourceValue("terminal_login"));
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DeviceInfo curConnectDeviceInfo = null;

    /**
     * 如果已经登录则直接跳转到相应页面，没登录则跳转到登录页面
     *
     * @param deviceInfo
     */
    private void connectDevice(DeviceInfo deviceInfo) {
        curConnectDeviceInfo = deviceInfo;
        Contacts.deviceOpt = deviceInfo;
        if (deviceInfo.isLogined()) {
            toLoginPage(deviceInfo);
            return;
        }
//        printLog(deviceInfo.getSn() + "/" + deviceInfo.getIpAddress() + "/" + deviceInfo.searchResult.tcpPort+"/"+deviceInfo.searchResult.encodeType);
        NovaOpt.GetInstance().connectDevice(deviceInfo.searchResult, new DefaultOnResultListener() {
            @Override
            public void onSuccess(Integer response) {
                Platform.runLater(() -> {
                    toLoginPage(curConnectDeviceInfo);
                });
            }

            @Override
            public void onError(ErrorDetail error) {
                //tip
                Platform.runLater(() -> {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, error.description);
                    errorAlert.setTitle(Contacts.getResourceValue("alert_title_error"));
                    errorAlert.setHeaderText(null);
                    errorAlert.showAndWait();
                });
                curConnectDeviceInfo = null;
            }
        }, wrapper -> {
            Platform.runLater(() -> {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, Contacts.getResourceValue
                        ("terminal_disconnect"));
                errorAlert.setTitle(Contacts.getResourceValue("alert_title_error"));
                errorAlert.setHeaderText(null);
                errorAlert.showAndWait();
            });
            curConnectDeviceInfo = null;
        });
    }

    /**
     * 注销登录
     *
     * @param event
     */
    @FXML
    protected void btn_logout(ActionEvent event) {
        if (null != selectedDeviceInfo) {
            boolean isLogout = NovaOpt.GetInstance().logOut(selectedDeviceInfo.getSn());
            if (isLogout) {
                DeviceInfo deviceInfo = ViewModel.getDeviceInfoBySN(selectedDeviceInfo.getSn());
                if (null != deviceInfo) {
                    //注销后修改注销按钮为不可用
                    deviceInfo.setLogined(false);
                    //重新计算未登录数量
                    setLabelUnlogin();
                    //更新listview
                    initListView();
                }
            }
        }
    }

    /**
     * 刷新操作，重新发UDP广播搜索屏体
     *
     * @param event
     */
    @FXML
    protected void btn_refresh_pressed(ActionEvent event) {
        btn_refresh.setDisable(true);
        searchScreen();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                btn_refresh.setDisable(false);
            }
        }, 3 * 1000);
    }

    /**
     * 指定IP搜索屏体
     *
     * @param event
     */
    @FXML
    protected void btn_Specify_Ip(ActionEvent event) {
        setLabelTotal();
        setLabelUnlogin();
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle(Contacts.getResourceValue("refresh_terminal_list_by_ip"));
        tid.setHeaderText(null);
        tid.setContentText(Contacts.getResourceValue("refresh_terminal_list_by_ip"));
        tid.getDialogPane().getButtonTypes().remove(0, 2);
        ButtonType btConfirm = new ButtonType(Contacts.getResourceValue("confirm"),
                ButtonBar.ButtonData.OK_DONE);
        ButtonType btCancel = new ButtonType(Contacts.getResourceValue("reboot_cancel"),
                ButtonBar.ButtonData.CANCEL_CLOSE);
        tid.getDialogPane().getButtonTypes().addAll(btConfirm, btCancel);

        Optional<String> result = tid.showAndWait();
        //方式1获取输入结果
        if (!result.isPresent()) {
            System.out.println("取消输入");
            return;
        }
        searchScreen(result.get());
    }

    /**
     * 修改界面语言后重启当前页面
     */
    private void reStartPage() {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("MainPage.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("SdkDemo");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            //传递参数
            MainPageController controller = fxmlLoader.getController();
            controller.initData();
            //region    查询设备
            controller.searchScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据绑定
     */
    public void initData() {
        ObservableList<String> languageOptions = FXCollections.observableArrayList(Contacts
                .getResourceValue("select_language_chinese"), Contacts.getResourceValue
                ("select_language_english"));
        cb_select_language.setItems(languageOptions);
        cb_select_language.getSelectionModel().select(Locale.CHINESE == Contacts
                .DEFAULT_LOCALE ? 0 : 1);
        cb_select_language.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int index = cb_select_language.getSelectionModel().getSelectedIndex();
                if (0 == index) {
                    if (Locale.CHINESE == Contacts.DEFAULT_LOCALE) {
                        return;
                    }
                    Contacts.DEFAULT_LOCALE = Locale.CHINESE;
                } else if (1 == index) {
                    if (Locale.ENGLISH == Contacts.DEFAULT_LOCALE) {
                        return;
                    }
                    Contacts.DEFAULT_LOCALE = Locale.ENGLISH;
                }
                updatePorperties();
                reStartPage();
                Platform.runLater(() -> {
                    Stage stage = (Stage) btn_refresh.getScene().getWindow();
                    stage.close();
                });
            }
        });
//        bindData();
        initListView();
    }

    private void updatePorperties() {
        Properties properties = new Properties();
        String path = System.getProperty("user.dir") + File.separator + "configuration.properties";
        File config = new File(path);
        try {
            if (!config.exists()) {
                config.createNewFile();
            }
            InputStream is = new BufferedInputStream(new FileInputStream(path));
            properties.load(is);
            properties.setProperty("local_language", Contacts.DEFAULT_LOCALE.getLanguage());
            FileOutputStream fos = new FileOutputStream(path, false);
            properties.store(fos, null);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindData() {
        try {
            snColumn.setCellValueFactory(cellData -> cellData.getValue().snProperty());
            deviceNameColumn.setCellValueFactory(cellData -> cellData.getValue()
                    .productNameProperty());
            aliasNameColumn.setCellValueFactory(cellData -> cellData.getValue().aliasNameProperty
                    ());
            ipAddressColumn.setCellValueFactory(cellData -> cellData.getValue().ipAddressProperty
                    ());
            connectBtnColumn.setCellValueFactory(cellData -> cellData.getValue()
                    .connectBtnProperty());
            deviceTable.setItems(ViewModel.devicesData);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void printLog(String log) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(Alert.AlertType.INFORMATION, log);
            errorAlert.setTitle("MSG");
            errorAlert.setHeaderText(null);
            errorAlert.showAndWait();
        });
    }
}
