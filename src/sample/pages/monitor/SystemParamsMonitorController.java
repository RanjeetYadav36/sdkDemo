package sample.pages.monitor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import novj.platform.vxkit.common.bean.ValueBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import sample.Contacts;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: Do what
 * @author: WangYuan
 * @date: 2019年04月28日 16:07
 */
public class SystemParamsMonitorController {
    @FXML
    TextArea taMsg;

    @FXML
    void OnGetCPUTemp(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getCpuTempData(Contacts.deviceOpt.getSn(), new OnResultListenerN<Float, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Float response) {
                showInfo("获取CPU温度成功！温度：" + response.toString());
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("获取CPU温度失败！" + error.description + "(" + error.errorCode + ")");
            }
        });
    }

    void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            taMsg.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
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
