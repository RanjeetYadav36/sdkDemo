package sample.pages.play;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import novj.platform.vxkit.common.bean.GlobalSpotsControlInfo;
import novj.platform.vxkit.common.bean.InsertPlayingConfig;
import novj.platform.vxkit.common.bean.programinfo.Source;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import sample.Contacts;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 插播控制
 * @author: WangYuan
 * @date: 2019年04月23日 17:19
 */
public class InsertPlayingCtrlPageController {

    @FXML
    ComboBox cbInsertPlayingEnable;
    @FXML
    Button btnSetInsertPlayingEnable;
    @FXML
    Button btnStopInsertPlaying;
    @FXML
    TextArea atOperationMsg;

    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("EnableInsertPlaying"), Contacts.getResourceValue("DisableInsertPlaying"));
        cbInsertPlayingEnable.setItems(openOptions);
        cbInsertPlayingEnable.getSelectionModel().select(0);
    }

    @FXML
    void OnSetInsertPlayingEnable(ActionEvent actionEvent) {
        GlobalSpotsControlInfo globalSpotsControlInfo = new GlobalSpotsControlInfo();
        Source source = new Source();
        source.setType(0);
        source.setPlatform(2);
        globalSpotsControlInfo.setSource(source);
        globalSpotsControlInfo.setEnable(cbInsertPlayingEnable.getSelectionModel().getSelectedIndex() == 0);
        NovaOpt.GetInstance().setGlobalSpotsEnable(Contacts.deviceOpt.getSn(), globalSpotsControlInfo, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                String msg = "设置插播使能成功！";
                showInfo(msg);
                showDialog(Alert.AlertType.INFORMATION, msg);
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                String msg = "设置插播使能失败！原因：" + error.description + "(" + error.errorCode + ")";
                showInfo(msg);
                showDialog(Alert.AlertType.ERROR, msg);
            }
        });
        /*
        if (cbInsertPlayingEnable.getSelectionModel().getSelectedIndex() == 0) {
            NovaOpt.GetInstance().enableInsertPlay(Contacts.deviceOpt.getSn(), new OnResultListenerN<Integer, ErrorDetail>() {
                @Override
                public void onSuccess(IRequestBase requestBase, Integer response) {
                    String msg = "设置插播使能成功！";
                    showInfo(msg);
                    showDialog(Alert.AlertType.INFORMATION, msg);
                }

                @Override
                public void onError(IRequestBase requestBase, ErrorDetail error) {
                    String msg = "设置插播使能失败！原因：" + error.description + "(" + error.errorCode + ")";
                    showInfo(msg);
                    showDialog(Alert.AlertType.ERROR, msg);
                }
            });
        } else {
            NovaOpt.GetInstance().disableInsertPlay(Contacts.deviceOpt.getSn(), new OnResultListenerN<Integer, ErrorDetail>() {
                @Override
                public void onSuccess(IRequestBase requestBase, Integer response) {
                    String msg = "设置插播使能成功！";
                    showInfo(msg);
                    showDialog(Alert.AlertType.INFORMATION, msg);
                }

                @Override
                public void onError(IRequestBase requestBase, ErrorDetail error) {
                    String msg = "设置插播使能失败！原因：" + error.description + "(" + error.errorCode + ")";
                    showInfo(msg);
                    showDialog(Alert.AlertType.ERROR, msg);
                }
            });
        }
        */
    }

    @FXML
    void OnGetInsertPlayingEnabled(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getGlobalSpotsEnable(Contacts.deviceOpt.getSn(), new OnResultListenerN<GlobalSpotsControlInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, GlobalSpotsControlInfo response) {
                if (response == null) {
                    String msg = "回读插播使能状态失败！原因：回复信息为空。";
                    showInfo(msg);
                    showDialog(Alert.AlertType.WARNING, msg);
                } else {
                    updateInsertPlayingEnabled(response.isEnable());
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                    showDialog(Alert.AlertType.INFORMATION, "回读插播使能状态成功。");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                String msg = "回读插播使能状态失败！原因：" + error.description + "(" + error.errorCode + ")";
                showInfo(msg);
                showDialog(Alert.AlertType.ERROR, msg);
            }
        });
        /*
        NovaOpt.GetInstance().getInsertPlayEnabled(Contacts.deviceOpt.getSn(), new OnResultListenerN<InsertPlayingConfig, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, InsertPlayingConfig response) {
                if (response == null) {
                    String msg = "回读插播使能状态失败！原因：回复信息为空。";
                    showInfo(msg);
                    showDialog(Alert.AlertType.WARNING, msg);
                } else {
                    updateInsertPlayingEnabled(response.isEnable());
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                    showDialog(Alert.AlertType.INFORMATION, "回读插播使能状态成功。");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                String msg = "回读插播使能状态失败！原因：" + error.description + "(" + error.errorCode + ")";
                showInfo(msg);
                showDialog(Alert.AlertType.ERROR, msg);
            }
        });
        */
    }

    @FXML
    void OnStopInsertPlaying(ActionEvent actionEvent) {
        NovaOpt.GetInstance().stopInsertPlay(Contacts.deviceOpt.getSn(),
                "", "", new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("设置成功");
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo(JSONUtil.toJsonStringByFastJson(error));
                    }
                });
    }

    void updateInsertPlayingEnabled(boolean enabled) {
        Platform.runLater(() -> {
            if (enabled) {
                cbInsertPlayingEnable.getSelectionModel().select(0);
            } else {
                cbInsertPlayingEnable.getSelectionModel().select(1);
            }
        });
    }

    void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            atOperationMsg.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
        });
    }

    void showDialog(Alert.AlertType alertType, String msgInfo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setHeaderText(msgInfo);
            alert.setResult(ButtonType.OK);
            alert.showAndWait();
        });
    }
}
