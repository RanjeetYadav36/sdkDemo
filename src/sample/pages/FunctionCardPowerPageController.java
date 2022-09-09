package sample.pages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import novj.platform.vxkit.common.bean.FunctionCardPowerBean;
import novj.platform.vxkit.common.bean.FunctionCardPowerStateBean;
import novj.platform.vxkit.common.bean.SourceBean;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import sample.Contacts;

/**
 * @description:
 * @date: 19-3-7 下午4:11
 * @author: jiahang
 */
public class FunctionCardPowerPageController {
    @FXML
    TextArea ta_show;
    @FXML
    TextField tf_power_switch;
    @FXML
    TextField tf_power_type;
    @FXML
    TextField tf_power_index;

    private List<FunctionCardPowerBean.DataBean.ConditionsBean> configs = new ArrayList<>();
    private FunctionCardPowerStateBean functionCardPowerStateBean;

    @FXML
    protected void onActionGetPowerState(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getFunctionCardPowerState(Contacts.deviceOpt.getSn(), new OnResultListenerN<FunctionCardPowerStateBean, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, FunctionCardPowerStateBean response) {
                functionCardPowerStateBean = response;
                showInfo(JSONUtil.toJsonStringByFastJson(response));
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                showInfo("Error:" + error.toString());
            }
        });
    }

    @FXML
    protected void onActionGetManualPower(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getFunctionCardManualPower(Contacts.deviceOpt.getSn(),
                new OnResultListenerN<FunctionCardPowerBean, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, FunctionCardPowerBean response) {
                        if (null != response) {
//                            functionCardPowerBean = response;
                            showInfo(JSONUtil.toJsonStringByFastJson(response));
                        } else {
                            showInfo("Error:获取电源配置信息失败");
                        }
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        if (null == error.description || error.description.trim().length() == 0) {
                            showInfo("Error:获取电源配置信息失败");
                        } else {
                            showInfo("Error:" + error.description);
                        }
                    }
                });
    }

    @FXML
    protected void onActionSetPower(ActionEvent actionEvent) {
        if (configs.size() == 0) {
            showInfo("Error:还没有添加电源配置信息");
            return;
        }

        if (functionCardPowerStateBean == null ||
                functionCardPowerStateBean.getCurrent_status_info() == null ||
                functionCardPowerStateBean.getCurrent_status_info().isEmpty() ||
                functionCardPowerStateBean.getCurrent_status_info().size() <= 0) {
            showInfo("Error:请先获取电源实时状态（需要多功能卡连接信息）");
            return;
        }
        //构造数据结构，仅供二次开发参考
        SourceBean sourceBean = new SourceBean();
        sourceBean.setPlatform(2);//CS
        sourceBean.setType(0);//第三方平台

        FunctionCardPowerBean.DataBean dataBean = new FunctionCardPowerBean.DataBean();
        dataBean.setConnectIndex(functionCardPowerStateBean.getCurrent_status_info().get(0).getConnectIndex());
        dataBean.setPortIndex(functionCardPowerStateBean.getCurrent_status_info().get(0).getPortIndex());
        List<FunctionCardPowerBean.DataBean> data = new ArrayList<>();
        data.add(dataBean);

        FunctionCardPowerBean functionCardPowerBean = new FunctionCardPowerBean();
        functionCardPowerBean.setSource(sourceBean);
        functionCardPowerBean.setType("FUNCTIONPOWER");//固定值
        functionCardPowerBean.setData(data);

        functionCardPowerBean.getData().get(0).setConditions(configs);

        NovaOpt.GetInstance().setFunctionCardPower(Contacts.deviceOpt.getSn(), functionCardPowerBean,
                new OnResultListenerN<Integer, ErrorDetail>() {
                    @Override
                    public void onSuccess(IRequestBase requestBase, Integer response) {
                        showInfo("电源信息设置成功");
                        configs.clear();
                    }

                    @Override
                    public void onError(IRequestBase requestBase, ErrorDetail error) {
                        showInfo("Error:电源信息设置失败:" + error.description);
                        configs.clear();
                    }
                });
    }

    @FXML
    protected void onActionAddConfig(ActionEvent actionEvent) {
        FunctionCardPowerBean.DataBean.ConditionsBean conditionsBean =
                new FunctionCardPowerBean.DataBean.ConditionsBean();
//        if (null == functionCardPowerBean) {
//            showInfo("Error:请先获取电源配置信息");
//            return;
//        }
        try {
            int powerSwitch = Integer.parseInt(tf_power_switch.getText());
            if (0 == powerSwitch || 1 == powerSwitch) {
                conditionsBean.setAction(powerSwitch);
            }
        } catch (NumberFormatException e) {
            showInfo("Error:电源开关参数错误");
            return;
        }
        if (null != tf_power_type.getText() && tf_power_type.getText().trim().length() > 0) {
            conditionsBean.setType(tf_power_type.getText());
        } else {
            showInfo("Error:电源类型不能为空");
            return;
        }
        try {
            int powerIndex = Integer.parseInt(tf_power_index.getText());
            conditionsBean.setPowerIndex(powerIndex);
        } catch (NumberFormatException e) {
            showInfo("Error:电源下标参数错误");
            return;
        }
        //防止重复添加
        boolean updatedExistPowerBean = false;
        for (FunctionCardPowerBean.DataBean.ConditionsBean addedPowerBean : configs) {
            if (addedPowerBean == null) {
                continue;
            }
            if (addedPowerBean.getPowerIndex() != conditionsBean.getPowerIndex()) {
                continue;
            }
            addedPowerBean.setAction(conditionsBean.getAction());
            addedPowerBean.setPowerIndex(conditionsBean.getPowerIndex());
            addedPowerBean.setType(conditionsBean.getType());
            updatedExistPowerBean = true;
            break;
        }
        if (!updatedExistPowerBean) {
            configs.add(conditionsBean);
        }
        showInfo("已经添加" + configs.size() + "条电源开关信息");
    }

    public void showInfo(String sInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ta_show.appendText("[" + df.format(day) + "]  " + sInfo + "\r\n");
        });
    }
}
