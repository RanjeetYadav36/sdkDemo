package uiexdata;

import beans.DeviceInfo;
import beans.PolicyInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author
 * */
public class ViewModel {
    public static ObservableList<PolicyInfo> policyInfos = FXCollections.observableArrayList();
    public static ObservableList<DeviceInfo> devicesData = FXCollections.observableArrayList();

    public static DeviceInfo getDeviceInfoBySN(String sn) {
        if(!sn.isEmpty()){
            for (DeviceInfo info:devicesData) {
                if(info.getSn().equals(sn)){
                    return info;
                }
            }
        }

        return null;
    }

}
