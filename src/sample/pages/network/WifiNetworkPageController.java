package sample.pages.network;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.monitor.StateBean;
import novj.platform.vxkit.common.bean.wifi.WifiListBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import novj.publ.util.StringUtil;
import sample.Contacts;

/**
 * @description:
 * @date: 19-3-8 下午3:01
 * @author: jiahang
 */
public class WifiNetworkPageController {
    @FXML
    ComboBox cb_wifi_set_status;
    @FXML
    Button btn_wifi_get_open, btn_wifi_get_list, btn_wifi_set_open, btn_wifi_connect;
    @FXML
    TextField tf_wifi_password, tf_wifi_ssid;
    @FXML
    TextArea ta_show;


    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cb_wifi_set_status.setItems(openOptions);
        cb_wifi_set_status.getSelectionModel().select(0);
    }


    @FXML
    protected void onActionWifiGet(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getWifiEnabled(Contacts.deviceOpt.getSn(), new OnResultListenerN<StateBean<Integer>, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, StateBean<Integer> response) {
                showInfo(1 == response.getState() ? "WIFI已打开" : "WIFI已关闭");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:查询失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiGetList(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getWifiList(Contacts.deviceOpt.getSn(), new OnResultListenerN<WifiListBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, WifiListBean response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("没有搜到WIFI");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:查询失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiSet(ActionEvent actionEvent) {
        boolean open = 0 == cb_wifi_set_status.getSelectionModel().getSelectedIndex() ? true : false;
        NovaOpt.GetInstance().setWifiEnabled(Contacts.deviceOpt.getSn(), open, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("设置成功");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:设置失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    protected void onActionWifiConnect(ActionEvent actionEvent) {
        if (StringUtil.isEmpty(tf_wifi_ssid.getText().trim()) ||
                StringUtil.isEmpty(tf_wifi_password.getText().trim())) {
            showInfo("参数错误");
            return;
        }
        NovaOpt.GetInstance().connectWifiNetwork(Contacts.deviceOpt.getSn(),
                tf_wifi_ssid.getText().trim(), tf_wifi_password.getText(),
                new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("设置成功");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        if (StringUtil.isEmpty(error.description)) {
                            showInfo("Error:设置失败");
                        } else {
                            showInfo("Error:" + error.description);
                        }
                    }
                });
    }


    public void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }
}
