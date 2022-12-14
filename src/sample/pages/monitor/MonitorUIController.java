package sample.pages.monitor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import novj.platform.vxkit.common.bean.monitor.*;
import novj.platform.vxkit.common.result.OnResultListenerN;
import novj.publ.api.NovaOpt;
import novj.publ.net.exception.ErrorDetail;
import novj.publ.net.svolley.Request.IRequestBase;
import novj.publ.util.JSONUtil;
import sample.Contacts;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 监控信息
 * @author: WangYuan
 * @date: 2019年04月17日 9:48
 */
public class MonitorUIController {
    static final String CONTAINER_COLOR = "#808080";
    static final String DEVICE_COLOR = "#0080ff";
    static final String DEVICE_EMPTY_COLOR="#666666";
    static final String NORMAL_DEVICE_COLOR = "#00ff00";
    static final String ERROR_DEVICE_COLOR = "#ff0000";
    static final String SELECTED_COLOR = "#FF0000";
    static final String UNSELECTED_COLOR = "#000000";

    static final int DEVICE_GAP = 5;
    static final int DEVICE_SENDER_WIDTH = 100;
    static final int DEVICE_SENDER_HEIGHT = 100;
    static final int DEVICE_SCANNER_WIDTH = 120;
    static final int DEVICE_SCANNER_HEIGHT = 120;

    @FXML
    ScrollPane spSenderCardContainer;
    @FXML
    ScrollPane spReceiverCardContainer;
    @FXML
    AnchorPane apSenderCardContainer;
    @FXML
    AnchorPane apReceiverCardContainer;
    @FXML
    TextArea taMonitorInfo;

    /**
     * 发送卡监控信息
     */
    SendCardMonitorInfo sendCardMonitorInfos;
    /**
     * 接收卡信息
     */
    ReceiveRegionInfo receiveRegionInfos;
    /**
     * 接收卡监控信息
     */
    ScreenMonitorDataBaseReceive receiveMonitorInfos;

    List<SenderCardLabel> senderCardLabelList = new ArrayList<>();
    List<ReceiveCardLabel> receiveCardLabelList = new ArrayList<>();

    HardwareDistribution.ScanBoardRegionInfo selectedReceiveCard = null;
    ReceiveCardLabel selectedReceiveCardLabel = null;

    public void init() {
        apSenderCardContainer.setBackground(new Background(new BackgroundFill(Color.web(CONTAINER_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
        apReceiverCardContainer.setBackground(new Background(new BackgroundFill(Color.web(CONTAINER_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    void onBtnRefreshSenderCardMonitorInfo(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getSendCardMonitorInfo(Contacts.deviceOpt.getSn(), new OnResultListenerN<SendCardMonitorInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, SendCardMonitorInfo response) {
                sendCardMonitorInfos = response;
                updateSenderCardsUI();
                showDialog(Alert.AlertType.INFORMATION, "提示:获取发送卡信息成功。");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (error == null) {
                    showInfo("刷新发送卡监控信息失败！原因：未知。");
                } else {
                    showInfo("刷新发送卡监控信息失败！原因：" + error.description + "(" + error.errorCode + ")。");
                }
                showDialog(Alert.AlertType.INFORMATION, "提示:获取发送卡信息失败。");
            }
        });
    }

    @FXML
    void onBtnGetReceiverCards(ActionEvent actionEvent) {
        NovaOpt.GetInstance().getReceiveCardRegionInfo(Contacts.deviceOpt.getSn(), new OnResultListenerN<ReceiveRegionInfo, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, ReceiveRegionInfo response) {
                receiveRegionInfos = response;
                updateReceiveCardUI();
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡信息成功。");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (error == null) {
                    showInfo("获取接收卡信息失败！原因：未知。");
                } else {
                    showInfo("获取接收卡信息失败！原因：" + error.description + "(" + error.errorCode + ")。");
                }
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡信息失败。");
            }
        });
    }

    @FXML
    void onBtnRefreshSingleCabinat(ActionEvent actionEvent) {
        if (receiveCardLabelList == null ||
                receiveCardLabelList.size() <= 0 ||
                receiveCardLabelList.isEmpty()) {
            showDialog(Alert.AlertType.WARNING, "警告：没有接收卡，请先刷新接收卡信息！");
            return;
        }
        for (ReceiveCardLabel receiveCard : receiveCardLabelList) {
            if (receiveCard.isSelected()) {
                selectedReceiveCard = receiveCard.getReceiveCardRegionInfo();
                selectedReceiveCardLabel = receiveCard;
                break;
            }
        }
        if (selectedReceiveCard == null || selectedReceiveCardLabel == null) {
            showDialog(Alert.AlertType.WARNING, "警告：请选择要刷新的接收卡！");
            return;
        }
        List<HardwareDistribution.ScanBoardRegionInfo> selectedReceiveCards = new ArrayList<>();
        selectedReceiveCards.add(selectedReceiveCard);
        ReceiveRegionInfo receiveRegionInfo = new ReceiveRegionInfo(selectedReceiveCards);
        NovaOpt.GetInstance().getMonitorInfoByReceiveCards(Contacts.deviceOpt.getSn(), receiveRegionInfo, new OnResultListenerN<ScreenMonitorDataBaseReceive, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, ScreenMonitorDataBaseReceive response) {
                if (response == null ||
                        response.getScreenMonitorData() == null ||
                        response.getScreenMonitorData().isEmpty() ||
                        response.getScreenMonitorData().size() <= 0) {
                    showInfo("获取接收卡监控信息成功！没有监控信息。");
                    selectedReceiveCardLabel.setReceiveCardMonitorData(null);
                } else {
                    showInfo("获取接收卡监控信息成功！");
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                    selectedReceiveCardLabel.setReceiveCardMonitorData(response.getScreenMonitorData().get(0));
                }
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡监控信息成功。");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (error == null) {
                    showInfo("获取监控信息失败！原因：未知。");
                } else {
                    showInfo("获取监控信息失败！原因：" + error.description + "(" + error.errorCode + ")。");
                }
                selectedReceiveCardLabel.setReceiveCardMonitorData(null);
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡监控信息失败。");
            }
        });
    }

    @FXML
    void onBtnRefreshAllCabinat(ActionEvent actionEvent) {
        if (receiveCardLabelList == null ||
                receiveCardLabelList.size() <= 0 ||
                receiveCardLabelList.isEmpty()) {
            showDialog(Alert.AlertType.WARNING, "警告：没有接收卡，请先刷新接收卡信息！");
            return;
        }
        List<HardwareDistribution.ScanBoardRegionInfo> selectedReceiveCards = new ArrayList<>();
        for (ReceiveCardLabel receiveCard : receiveCardLabelList) {
            selectedReceiveCards.add(receiveCard.getReceiveCardRegionInfo());
        }
        ReceiveRegionInfo receiveRegionInfo = new ReceiveRegionInfo(selectedReceiveCards);
        NovaOpt.GetInstance().getMonitorInfoByReceiveCards(Contacts.deviceOpt.getSn(), receiveRegionInfo, new OnResultListenerN<ScreenMonitorDataBaseReceive, ErrorDetail>() {
            @Override
            public void onSuccess(IRequestBase requestBase, ScreenMonitorDataBaseReceive response) {
                if (response == null ||
                        response.getScreenMonitorData() == null ||
                        response.getScreenMonitorData().isEmpty() ||
                        response.getScreenMonitorData().size() <= 0) {
                    showInfo("获取接收卡监控信息成功！没有监控信息。");
                    for (ReceiveCardLabel receiveCard : receiveCardLabelList) {
                        receiveCard.setReceiveCardMonitorData(null);
                    }
                } else {
                    showInfo("获取接收卡监控信息成功！");
                    showInfo(JSONUtil.toJsonStringByFastJson(response));
                    ArrayList<ReceiveCardMonitorData> receiveCardMonitorDatas = response.getScreenMonitorData();
                    int index = 0;
                    for (ReceiveCardMonitorData receiveCardMonitorData : receiveCardMonitorDatas) {
                        receiveCardLabelList.get(index).setReceiveCardMonitorData(receiveCardMonitorData);
                        index++;
                    }
                }
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡监控信息成功。");
            }

            @Override
            public void onError(IRequestBase requestBase, ErrorDetail error) {
                if (error == null) {
                    showInfo("获取接收卡监控信息失败！原因：未知。");
                } else {
                    showInfo("获取接收卡监控信息失败！原因：" + error.description + "(" + error.errorCode + ")。");
                }
                for (ReceiveCardLabel receiveCard : receiveCardLabelList) {
                    receiveCard.setReceiveCardMonitorData(null);
                }
                showDialog(Alert.AlertType.INFORMATION, "提示:获取接收卡监控信息失败。");
            }
        });
    }

    void updateSenderCardsUI() {
        Platform.runLater(() -> {
            updateSenderCardsUI(sendCardMonitorInfos);
        });
    }

    void updateSenderCardsUI(SendCardMonitorInfo senderCardMonitorInfos) {
        apSenderCardContainer.getChildren().clear();
        senderCardLabelList.clear();
        if (senderCardMonitorInfos == null ||
                senderCardMonitorInfos.getSendCardMonitorInfo() == null ||
                senderCardMonitorInfos.getSendCardMonitorInfo().isEmpty() ||
                senderCardMonitorInfos.getSendCardMonitorInfo().size() <= 0) {
            showInfo("刷新发送卡监控信息成功！无发送卡信息。");
            apSenderCardContainer.setPrefWidth(spSenderCardContainer.getPrefWidth());
            apSenderCardContainer.setPrefHeight(spSenderCardContainer.getPrefHeight());
        } else {
            showInfo("刷新发送卡监控信息成功！");
            showInfo(JSONUtil.toJsonStringByFastJson(senderCardMonitorInfos));
            List<SenderMonitorInfo> sendCardMonitorInfoList = senderCardMonitorInfos.getSendCardMonitorInfo();
            int addIndex = 0;
            for (SenderMonitorInfo senderMonitorInfo : sendCardMonitorInfoList) {
                SenderCardLabel senderCard = new SenderCardLabel();
                senderCard.setSenderMonitorInfo(senderMonitorInfo);
                senderCardLabelList.add(senderCard);
                senderCard.setOnMouseClicked(event -> {
                    for (SenderCardLabel label : senderCardLabelList) {
                        if (label == senderCard) {
                            label.setSelected(true/*!label.isSelected()*/);
                        } else {
                            label.setSelected(false);
                        }
                        if (label.isSelected()) {
                            label.setTextFill(Color.web(SELECTED_COLOR));
                        } else {
                            label.setTextFill(Color.web(UNSELECTED_COLOR));
                        }
                    }
                });
                MenuItem menuItem = new MenuItem(Contacts.getResourceValue("OpenDetailInfo"));
                ContextMenu contextMenu = new ContextMenu(menuItem);
                senderCard.setContextMenu(contextMenu);
                menuItem.setOnAction(event -> {
                    if (senderCard.getSenderMonitorInfo() == null) {
                        showDialog(Alert.AlertType.WARNING, "警告：该发送卡没有监控信息！");
                        return;
                    }
                    showSenderCardDetail(senderCard.getSenderMonitorInfo());
                });
                if (senderMonitorInfo == null) {
                    senderCard.setBackground(new Background(new BackgroundFill(Color.web(DEVICE_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    switch (senderMonitorInfo.getDevWorkStatus()) {
                        case Utils.DEVICE_WORK_STATUS_OK:
                            senderCard.setBackground(new Background(new BackgroundFill(Color.web(NORMAL_DEVICE_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                            break;
                        case Utils.DEVICE_WORK_STATUS_ERROR:
                            senderCard.setBackground(new Background(new BackgroundFill(Color.web(ERROR_DEVICE_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                            break;
                        default:
                            senderCard.setBackground(new Background(new BackgroundFill(Color.web(DEVICE_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                            break;
                    }
                    senderCard.setText(Utils.getSenderCardShortMsg(senderMonitorInfo));
                }
                senderCard.setLayoutX(addIndex * (DEVICE_SENDER_WIDTH + DEVICE_GAP));
                senderCard.setLayoutY(0);
                senderCard.setPrefWidth(DEVICE_SENDER_WIDTH);
                senderCard.setPrefHeight(DEVICE_SENDER_HEIGHT);
                apSenderCardContainer.getChildren().add(senderCard);
                addIndex++;
            }
            int usedWidth = addIndex * (DEVICE_SENDER_WIDTH + DEVICE_GAP) - DEVICE_GAP;
            apSenderCardContainer.setPrefWidth(spSenderCardContainer.getPrefWidth() > usedWidth ? spSenderCardContainer.getPrefWidth() : usedWidth);
            apSenderCardContainer.setPrefHeight(spSenderCardContainer.getPrefHeight());
        }
    }

    void updateReceiveCardUI() {
        Platform.runLater(() -> {
            updateReceiveCardUI(receiveRegionInfos);
        });
    }

    void updateReceiveCardUI(ReceiveRegionInfo receiveRegionInfos) {
        apReceiverCardContainer.getChildren().clear();
        receiveCardLabelList.clear();
        if (receiveRegionInfos == null ||
                receiveRegionInfos.getReceiveCardRegionInfo() == null ||
                receiveRegionInfos.getReceiveCardRegionInfo().isEmpty() ||
                receiveRegionInfos.getReceiveCardRegionInfo().size() <= 0) {
            showInfo("刷新接收卡信息成功！无接收卡信息。");
            apReceiverCardContainer.setPrefWidth(spReceiverCardContainer.getPrefWidth());
            apReceiverCardContainer.setPrefHeight(spReceiverCardContainer.getPrefHeight());
        } else {
            showInfo("刷新接收卡信息成功！");
            showInfo(JSONUtil.toJsonStringByFastJson(receiveRegionInfos));
            List<HardwareDistribution.ScanBoardRegionInfo> scanBoardRegionInfoList = receiveRegionInfos.getReceiveCardRegionInfo();
            scanBoardRegionInfoList.sort((scanner1, scanner2) -> {
                if (scanner1.rowIndexInScreen < scanner2.rowIndexInScreen) {
                    return -1;
                } else if (scanner1.rowIndexInScreen == scanner2.rowIndexInScreen) {
                    if (scanner1.colIndexInScreen < scanner2.colIndexInScreen) {
                        return -1;
                    } else if (scanner1.colIndexInScreen == scanner2.colIndexInScreen) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }
            });
            int maxWidth = 0;
            int maxHeight = 0;
            for (HardwareDistribution.ScanBoardRegionInfo scanBoardRegionInfo : scanBoardRegionInfoList) {
                ReceiveCardLabel receiveCard = new ReceiveCardLabel();
                receiveCard.setReceiveCardRegionInfo(scanBoardRegionInfo);
                receiveCardLabelList.add(receiveCard);
                receiveCard.setOnMouseClicked(event -> {
                    for (ReceiveCardLabel label : receiveCardLabelList) {
                        if (label == receiveCard) {
                            label.setSelected(true/*!label.isSelected()*/);
                        } else {
                            label.setSelected(false);
                        }
                        if (label.isSelected()) {
                            label.setTextFill(Color.web(SELECTED_COLOR));
                        } else {
                            label.setTextFill(Color.web(UNSELECTED_COLOR));
                        }
                    }
                });
                MenuItem menuItem = new MenuItem(Contacts.getResourceValue("OpenDetailInfo"));
                ContextMenu contextMenu = new ContextMenu(menuItem);
                receiveCard.setContextMenu(contextMenu);
                menuItem.setOnAction(event -> {
                    if (receiveCard.getReceiveCardMonitorData() == null) {
                        showDialog(Alert.AlertType.WARNING, "警告：该接收卡没有监控信息！");
                        return;
                    }
                    showReceiveCardMonitorDetail(receiveCard.getReceiveCardMonitorData());
                });
                if (scanBoardRegionInfo != null) {
                    receiveCard.setText(Utils.getReceiveCardShortMsg(scanBoardRegionInfo));
                }
                if(scanBoardRegionInfo.senderIndex==-1){
                    receiveCard.setBackground(new Background(new BackgroundFill(Color.web(DEVICE_EMPTY_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                }else{
                    receiveCard.setBackground(new Background(new BackgroundFill(Color.web(DEVICE_COLOR), CornerRadii.EMPTY, Insets.EMPTY)));
                }
                int x = scanBoardRegionInfo.colIndexInScreen * (DEVICE_SCANNER_WIDTH + DEVICE_GAP);
                int y = scanBoardRegionInfo.rowIndexInScreen * (DEVICE_SCANNER_HEIGHT + DEVICE_GAP);
                receiveCard.setLayoutX(x);
                receiveCard.setLayoutY(y);
                receiveCard.setPrefWidth(DEVICE_SCANNER_WIDTH);
                receiveCard.setPrefHeight(DEVICE_SCANNER_HEIGHT);
                apReceiverCardContainer.getChildren().add(receiveCard);
                if (maxWidth < x + DEVICE_SCANNER_WIDTH) {
                    maxWidth = x + DEVICE_SCANNER_WIDTH;
                }
                if (maxHeight < y + DEVICE_SCANNER_HEIGHT) {
                    maxHeight = y + DEVICE_SCANNER_HEIGHT;
                }
            }
            apReceiverCardContainer.setPrefWidth(spReceiverCardContainer.getPrefWidth() > maxWidth ? spReceiverCardContainer.getPrefWidth() : maxWidth);
            apReceiverCardContainer.setPrefHeight(spReceiverCardContainer.getPrefHeight() > maxHeight ? spReceiverCardContainer.getPrefHeight() : maxHeight);
        }
    }

    void showSenderCardDetail(SenderMonitorInfo senderMonitorInfo) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/monitor/SenderMonitorInfoUI.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("发送卡详细信息");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            SenderMonitorInfoUIController controller = fxmlLoader.getController();
            controller.init(senderMonitorInfo);
        } catch (Exception e) {
            e.printStackTrace();
            showInfo(e.getMessage());
        }
    }

    void showReceiveCardMonitorDetail(ReceiveCardMonitorData receiveCardMonitorData) {
        try {
            Stage primaryStage = new Stage();
            URL location = getClass().getResource("/sample/pages/monitor/ReceiveCardMonitorInfoUI.fxml");
            FXMLLoader fxmlLoader = Contacts.getFxmlLoader(location);
            Parent root = fxmlLoader.load();

            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setTitle("接收卡详细信息");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            ReceiveCardMonitorInfoUIController controller = fxmlLoader.getController();
            controller.init(receiveCardMonitorData);
        } catch (Exception e) {
            e.printStackTrace();
            showInfo(e.getMessage());
        }
    }

    void showInfo(String msgInfo) {
        Platform.runLater(() -> {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            taMonitorInfo.appendText("[" + df.format(day) + "]  " + msgInfo + "\r\n");
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
