package sample.pages.app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.platform.vxkit.handy.net.transfer.OnFileTransferListener;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import sample.Contacts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: Do what
 * @author: WangYuan
 * @date: 2019年04月28日 10:52
 */
public class AppManagerPageController {
    @FXML
    Button btnUpdateSystem;
    @FXML
    Button btnUpdateSoftware;
    @FXML
    Button btnInstallAPK;
    @FXML
    Button btnUninstallAPK;
    @FXML
    TextField txtUninstallPackName;
    @FXML
    TextArea taMsg;

    public void updateLanguage() {
        btnUpdateSystem.setText(Contacts.getResourceValue("terminal_update_system"));
        btnUpdateSoftware.setText(Contacts.getResourceValue("terminal_update_software"));
        btnInstallAPK.setText(Contacts.getResourceValue("update_apkfile"));
    }

    @FXML
    void OnUpdateSystem(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_system"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btnUpdateSystem.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSystem(Contacts.deviceOpt.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("已下发: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("开始升级系统");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("系统升级成功");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("系统升级失败：" + error.description);
            }
        });
    }

    @FXML
    void OnUpdateSoftware(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("terminal_update_software"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("nuzip(*.nuzip)", "*.nuzip"));
        File systemFile = fileChooser.showOpenDialog(btnUpdateSoftware.getScene().getWindow());
        if (null == systemFile) {
            return;
        }
        NovaOpt.GetInstance().startUpdateSoftware(Contacts.deviceOpt.getSn(), systemFile, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("已下发: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("开始升级软件");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("软件升级成功");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("软件升级失败：" + error.description);
            }
        });
    }

    @FXML
    void OnInstallAPK(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Contacts.getResourceValue("select_apk_file"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("APK(*.apk)", "*.apk"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("所有文件", "*.*"));
        File apkFile = fileChooser.showOpenDialog(btnInstallAPK.getScene().getWindow());
        if (null == apkFile) {
            return;
        }
        String packageName = apkFile.getName();//update.apk
        NovaOpt.GetInstance().startUploadApk(Contacts.deviceOpt.getSn(), apkFile, packageName, true, new OnFileTransferListener() {
            @Override
            public void onTransferred(long length) {
                showInfo("已下发: " + length + "byte");
            }

            @Override
            public void onStartTransfer() {
                showInfo("开始升级APK");
            }

            @Override
            public void onSuccess(Integer response) {
                showInfo("APK升级成功");
            }

            @Override
            public void onError(ErrorDetail error) {
                showInfo("APK升级失败: " + error.description + "," + error.errorCode);
            }
        });
    }

    @FXML
    void OnUninstallAPK(ActionEvent actionEvent) {
        String uninstallPackName = txtUninstallPackName.getText();
        if (uninstallPackName == null || uninstallPackName.isEmpty()) {
            showInfo("请输入要卸载的APK的包名！");
            return;
        }
        NovaOpt.GetInstance().uninstallPackage(Contacts.deviceOpt.getSn(), uninstallPackName, new OnResultListenerN<Integer, ErrorDetail>() {

            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("卸载APK成功！");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("卸载APK失败！" + error.description + "(" + error.errorCode + ")");
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
