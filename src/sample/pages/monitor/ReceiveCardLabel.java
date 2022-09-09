package sample.pages.monitor;

import javafx.scene.control.Label;
import novj.platform.vxkit.common.bean.monitor.HardwareDistribution;
import novj.platform.vxkit.common.bean.monitor.ReceiveCardMonitorData;

/**
 * @Description: Do what
 * @author: WangYuan
 * @date: 2019年04月19日 9:47
 */
public class ReceiveCardLabel extends Label {
    private HardwareDistribution.ScanBoardRegionInfo receiveCardRegionInfo;
    private ReceiveCardMonitorData receiveCardMonitorData;
    private boolean isSelected=false;

    /**
     * 接收卡信息
     */
    public HardwareDistribution.ScanBoardRegionInfo getReceiveCardRegionInfo() {
        return receiveCardRegionInfo;
    }

    public void setReceiveCardRegionInfo(HardwareDistribution.ScanBoardRegionInfo receiveCardRegionInfo) {
        this.receiveCardRegionInfo = receiveCardRegionInfo;
    }

    /**
     * 是否被选中
     */
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * 接收卡监控信息
     */
    public ReceiveCardMonitorData getReceiveCardMonitorData() {
        return receiveCardMonitorData;
    }

    public void setReceiveCardMonitorData(ReceiveCardMonitorData receiveCardMonitorData) {
        this.receiveCardMonitorData = receiveCardMonitorData;
    }
}
