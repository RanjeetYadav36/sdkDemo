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
import novj.platform.vxkit.common.bean.APOpen;
import novj.platform.vxkit.common.bean.WifiApAccount;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import novj.publ.util.StringUtil;
import sample.Contacts;

/**
 * @description:
 * @date: 19-3-8 下午3:02
 * @author: jiahang
 */
public class APNetworkPageController {
    @FXML
    TextArea ta_show;
    @FXML
    ComboBox cb_ap_open;
    @FXML
    TextField tf_ap_alias, tf_ap_suffix, tf_ap_password;
    @FXML
    Button btn_ap_get_open, btn_ap_get, btn_ap_set_open, btn_ap_set;


    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cb_ap_open.setItems(openOptions);
        cb_ap_open.getSelectionModel().select(0);
    }

    /**
     * 获取AP配置信息
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionAPGet(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getAPNetwork(Contacts.deviceOpt.getSn(), new OnResultListenerN<WifiApAccount, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, WifiApAccount response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("配置信息为空");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:获取配置失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    /**
     * 设置AP信息
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionAPSet(ActionEvent actionEvent) {
        WifiApAccount wifiApAccount = new WifiApAccount();
        if (StringUtil.isEmpty(tf_ap_alias.getText().trim()) ||
                StringUtil.isEmpty(tf_ap_suffix.getText().trim()) ||
                StringUtil.isEmpty(tf_ap_password.getText().trim())) {
            showInfo("参数错误");
            return;
        }
        wifiApAccount.aliasName = tf_ap_alias.getText().trim();
        wifiApAccount.suffix = tf_ap_suffix.getText().trim();
        wifiApAccount.password = tf_ap_password.getText().trim();

        NovaOpt.GetInstance().setAPNetwork(Contacts.deviceOpt.getSn(), wifiApAccount, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("配置成功");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:配置失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    /**
     * 获取AP开关状态
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionAPGetOpen(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getAPNetworkStatus(Contacts.deviceOpt.getSn(),
                new OnResultListenerN<APOpen, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, APOpen response) {
                        showInfo(response.isEnable()+"");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        if (StringUtil.isEmpty(error.description)) {
                            showInfo("Error:配置失败");
                        } else {
                            showInfo("Error:" + error.description);
                        }
                    }
                });
    }

    /**
     * 设置AP开启状态
     *
     * @param actionEvent
     */
    @FXML
    protected void onActionAPSetOpen(ActionEvent actionEvent) {
        boolean status = 0 == cb_ap_open.getSelectionModel().getSelectedIndex()?true:false;
        NovaOpt.GetInstance().setAPNetworkStatus(Contacts.deviceOpt.getSn(), status, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("配置成功");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:配置失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    public void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
        });
    }
}
