package sample.pages.monitor;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import novj.platform.vxkit.common.bean.monitor.DeviceSearchMapping;
import novj.platform.vxkit.common.bean.monitor.SenderMonitorInfo;
import sample.Contacts;

import java.util.List;

/**
 * @Description: 显示发送卡监控信息
 * @author: WangYuan
 * @date: 2019年04月18日 13:47
 */
public class SenderMonitorInfoUIController {
    @FXML
    TreeView tvSenderMonitorInfo;

    public void init(SenderMonitorInfo senderMonitorInfo) {
        if (tvSenderMonitorInfo.getRoot() != null) {
            tvSenderMonitorInfo.getRoot().getChildren().clear();
        }
        TreeItem root = new TreeItem(Contacts.getResourceValue("DetailInfos"));
        tvSenderMonitorInfo.setRoot(root);
        String workStaus = Contacts.getResourceValue("WorkStatus") + "：" + Utils.getDeviceWorkStatus(senderMonitorInfo.getDevWorkStatus());
        root.getChildren().add(new TreeItem(workStaus));
        String isDVIConnected = Contacts.getResourceValue("SendCard_DVIConnectedStatus") + (senderMonitorInfo.isDVIConnected() ? Contacts.getResourceValue("Connected") : Contacts.getResourceValue("Disconnected"));
        root.getChildren().add(new TreeItem(isDVIConnected));
        if (senderMonitorInfo.isDVIConnected()) {
            String dviRate = Contacts.getResourceValue("SendCard_DVIRate") + senderMonitorInfo.getDVIRate();
            root.getChildren().add(new TreeItem(dviRate));
        }
        TreeItem tempTreeItem = new TreeItem(Contacts.getResourceValue("DevicePosition"));
        root.getChildren().add(tempTreeItem);
        List<DeviceSearchMapping> deviceSearchMappings = senderMonitorInfo.getDevMappingList();
        if (deviceSearchMappings == null || deviceSearchMappings.isEmpty() || deviceSearchMappings.size() <= 0) {
            tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDevicePosition")));
        } else {
            int index = 0;
            for (DeviceSearchMapping deviceSearchMapping : deviceSearchMappings) {
                if (deviceSearchMapping == null) {
                    //tempTreeItem.getChildren().add(new TreeItem("设备位置信息" + index + "为空"));
                } else {
                    TreeItem addTreeItem = new TreeItem(Contacts.getResourceValue("DevicePosition") + deviceSearchMapping.getDevIndex()/*index*/);
                    addTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("PositionIndex") + deviceSearchMapping.getDevIndex()));
                    addTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceType") + Utils.getDeviceName(deviceSearchMapping.getDevType())));
                    tempTreeItem.getChildren().add(addTreeItem);
                }
                index++;
            }
        }
        tempTreeItem = new TreeItem(Contacts.getResourceValue("NetPortInfo"));
        root.getChildren().add(tempTreeItem);
        List<SenderMonitorInfo.PortOfSenderMonitorInfo> portOfSenderMonitorInfos = senderMonitorInfo.getReduPortOfSenderList();
        if (portOfSenderMonitorInfos == null || portOfSenderMonitorInfos.isEmpty() || portOfSenderMonitorInfos.size() <= 0) {
            tempTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("NoNetPortInfo")));
        } else {
            int index = 0;
            for (SenderMonitorInfo.PortOfSenderMonitorInfo portOfSenderMonitorInfo : portOfSenderMonitorInfos) {
                if (portOfSenderMonitorInfo == null) {
                    //tempTreeItem.getChildren().add(new TreeItem("网口" + index + "为空"));
                } else {
                    TreeItem addTreeItem = new TreeItem(Contacts.getResourceValue("NetPort") + index);
                    tempTreeItem.getChildren().add(addTreeItem);
                    addTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("WorkStatus") + "：" + Utils.getDeviceWorkStatus(portOfSenderMonitorInfo.getDevWorkStatus())));
                    addTreeItem.getChildren().add(new TreeItem(Contacts.getResourceValue("RedundantStatus") + "：" + (portOfSenderMonitorInfo.isRedundant ? Contacts.getResourceValue("yes") : Contacts.getResourceValue("no"))));
                    TreeItem devices = new TreeItem(Contacts.getResourceValue("DevicePosition"));
                    addTreeItem.getChildren().add(devices);
                    List<DeviceSearchMapping> deviceSearchMappingList = portOfSenderMonitorInfo.getDevMappingList();
                    if (deviceSearchMappingList == null || deviceSearchMappingList.isEmpty() || deviceSearchMappingList.size() <= 0) {
                        devices.getChildren().add(new TreeItem(Contacts.getResourceValue("NoDevicePosition")));
                    } else {
                        int index2 = 0;
                        for (DeviceSearchMapping deviceSearchMapping : deviceSearchMappingList) {
                            if (deviceSearchMapping == null) {
                                //devices.getChildren().add(new TreeItem("设备位置信息" + index2 + "为空"));
                            } else {
                                TreeItem tempTreeItem2 = new TreeItem(Contacts.getResourceValue("DevicePosition") + deviceSearchMapping.getDevIndex()/*index2*/);
                                devices.getChildren().add(tempTreeItem2);
                                tempTreeItem2.getChildren().add(new TreeItem(Contacts.getResourceValue("PositionIndex") + deviceSearchMapping.getDevIndex()));
                                tempTreeItem2.getChildren().add(new TreeItem(Contacts.getResourceValue("DeviceType") + Utils.getDeviceName(deviceSearchMapping.getDevType())));
                            }
                            index2++;
                        }
                    }
                }
                index++;
            }
        }
    }
}
