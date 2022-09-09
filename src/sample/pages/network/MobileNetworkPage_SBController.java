package sample.pages.network;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import novj.platform.vxkit.common.bean.MobileData;
import novj.platform.vxkit.common.bean.apn.APNData;
import novj.platform.vxkit.common.bean.apn.APNDataBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import novj.publ.util.StringUtil;
import sample.Contacts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangyuan
 * @ClassName:
 * @Description:
 * @Date: 2019-03-28
 */
public class MobileNetworkPage_SBController {
    @FXML
    private TextField tfMobileNetProviderName;
    @FXML
    private ComboBox cbIsOpenMobileNet, cbIsOpenRoaming, cbIsEnableMobileNet;
//    @FXML
//    private Button btnCheckMobileModuleExist, btnGetMobileNetConfig, btnSetMobileNetConfig;
    @FXML
    private TextField tfCrarier, tfmcc, tfmnc, tfapn, tfuser, tfserver, tfproxy, tfport, tfmmsc, tfmmsproxy, tfmmsport,
            tftype, tfprotocol, tfauthtype, tfroamingprotocol;
    @FXML
    private PasswordField pfpassword;
    @FXML
    private ComboBox cbisUserDefined, cbisUsed;
//    @FXML
//    private Button btnGetAPNConfig, btnSetAPNConfig;
    @FXML
    private TextArea taOperationInfo;

    public void init() {
        ObservableList<String> openOptions = FXCollections.observableArrayList(
                Contacts.getResourceValue("yes"), Contacts.getResourceValue("no"));
        cbIsOpenMobileNet.setItems(openOptions);
        cbIsOpenMobileNet.getSelectionModel().select(0);
        cbIsOpenRoaming.setItems(openOptions);
        cbIsOpenRoaming.getSelectionModel().select(0);
        cbIsEnableMobileNet.setItems(openOptions);
        cbIsEnableMobileNet.getSelectionModel().select(0);

        cbisUserDefined.setItems(openOptions);
        cbisUserDefined.getSelectionModel().select(0);
        cbisUsed.setItems(openOptions);
        cbisUsed.getSelectionModel().select(0);
    }

    private void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            taOperationInfo.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }

    @FXML
    private void onCheckMobileModuleExist(ActionEvent actionEvent) {
        NovaOpt.GetInstance().isMobileModuleExisted(Contacts.deviceOpt.getSn(), new OnResultListenerN<Boolean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Boolean response) {
                showInfo(response ? "存在" : "不存在");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:检查失败");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }

    @FXML
    private void onGetMobileNetConfig(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getMobileNetwork(Contacts.deviceOpt.getSn(), new OnResultListenerN<MobileData, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, MobileData response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("Error:获取配置失败");
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

    @FXML
    private void onSetMobileNetConfig(ActionEvent actionEvent) {
        MobileData.BasicConfig basicConfig = new MobileData.BasicConfig();
        basicConfig.setMobileData(0 == cbIsOpenMobileNet.getSelectionModel().getSelectedIndex() ?
                true : false);
        basicConfig.setDataRoaming(0 == cbIsOpenRoaming.getSelectionModel().getSelectedIndex() ?
                true : false);
        basicConfig.setEnable4G(0 == cbIsEnableMobileNet.getSelectionModel().getSelectedIndex() ?
                true : false);

        MobileData.Advanced advanced = new MobileData.Advanced();
        advanced.setNetworkType("AUTO");
//        if (StringUtil.isEmpty(tfMobileNetProviderName.getText().trim())) {
        MobileData.APN apn = new MobileData.APN();
        apn.setProviderName(tfMobileNetProviderName.getText().trim());
        advanced.setAPN(apn);
//        }

        MobileData mobileData = new MobileData();
        mobileData.setBasicConfigs(basicConfig);
        mobileData.setAdvanced(advanced);

        NovaOpt.GetInstance().setMobileNetwork(Contacts.deviceOpt.getSn(), mobileData, new OnResultListenerN<Integer, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, Integer response) {
                showInfo("设置成功");
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

    @FXML
    private void onGetAPNConfig(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getMobileNetworkAPN(Contacts.deviceOpt.getSn(), new OnResultListenerN<APNDataBean, ErrorDetail>() {

            @Override
            public void onSuccess(IRequestBase requestBase, APNDataBean response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("Error:获取配置失败");
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

    @FXML
    private void onSetAPNConfig(ActionEvent actionEvent) {
        String carrier = tfCrarier.getText();
        String mcc = tfmcc.getText();
        String mnc = tfmnc.getText();
        String apn = tfapn.getText();
        if (carrier == null || carrier.isEmpty() ||
                mcc == null || mcc.isEmpty() ||
                mnc == null || mnc.isEmpty() ||
                apn == null || apn.isEmpty()) {
            showInfo("红色标识为必填参数！");
            return;
        }
        APNData apnData = new APNData();
        apnData.setCarrier(tfCrarier.getText());
        apnData.setMcc(tfmcc.getText());
        apnData.setMnc(tfmnc.getText());
        apnData.setApn(tfapn.getText());
        apnData.setUser(tfuser.getText());
        apnData.setPassword(pfpassword.getText());
        apnData.setServer(tfserver.getText());
        apnData.setProxy(tfproxy.getText());
        apnData.setPort(tfport.getText());
        apnData.setMmsc(tfmmsc.getText());
        apnData.setMmsproxy(tfmmsproxy.getText());
        apnData.setMmsport(tfmmsport.getText());
        apnData.setType(tftype.getText());
        apnData.setProtocol(tfprotocol.getText());
        apnData.setAuthtype(tfauthtype.getText());
        apnData.setRoamingProtocol(tfroamingprotocol.getText());
        apnData.setUserDefined(cbisUserDefined.getSelectionModel().getSelectedIndex() == 0 ? true : false);
        apnData.setUsed(cbisUsed.getSelectionModel().getSelectedIndex() == 0 ? true : false);
        List<APNData> apns = new ArrayList<>();
        apns.add(apnData);
        APNDataBean apnDataBean = new APNDataBean();
        apnDataBean.setApnSettings(apns);
        NovaOpt.GetInstance().setMobileNetworkAPN(Contacts.deviceOpt.getSn(), apnDataBean, new OnResultListenerN<MobileData, ErrorDetail>() {

            @Override
            public void onSuccess(IRequestBase requestBase, MobileData response) {
                if (null != response) {
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                } else {
                    showInfo("Error:设置失败");
                }
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (StringUtil.isEmpty(error.description)) {
                    showInfo("Error:失败信息为空");
                } else {
                    showInfo("Error:" + error.description);
                }
            }
        });
    }
}
