package sample.pages.monitor;

import javafx.scene.control.Label;
import novj.platform.vxkit.common.bean.monitor.SenderMonitorInfo;

/**
 * @Description: Do what
 * @author: WangYuan
 * @date: 2019年04月19日 9:39
 */
public class SenderCardLabel extends Label {
    private SenderMonitorInfo senderMonitorInfo;
    private boolean isSelected=false;

    /**
     * 发送卡信息
     */
    public SenderMonitorInfo getSenderMonitorInfo() {
        return senderMonitorInfo;
    }

    public void setSenderMonitorInfo(SenderMonitorInfo senderMonitorInfo) {
        this.senderMonitorInfo = senderMonitorInfo;
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
}
