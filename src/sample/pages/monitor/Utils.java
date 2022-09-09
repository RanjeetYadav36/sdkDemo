package sample.pages.monitor;

import novj.platform.vxkit.common.bean.monitor.DeviceSearchMapping;
import novj.platform.vxkit.common.bean.monitor.HardwareDistribution;
import novj.platform.vxkit.common.bean.monitor.SenderMonitorInfo;
import sample.Contacts;

import java.util.List;

/**
 * @Description: 监控公共类
 * @author: WangYuan
 * @date: 2019年04月18日 14:17
 */
public class Utils {

    /**
     * 未定义设备
     */
    public static final int UNDEFINED = 0;
    /**
     * LED屏
     */
    public static final int LED_SCREEN = 1;
    /**
     * 发送卡
     */
    public static final int SEND_CARD = 2;
    /**
     * 发送卡网口
     */
    public static final int SEND_CARD_PORT = 3;
    /**
     * 分线器
     */
    public static final int DECONCENT_CARD = 4;
    /**
     * 接收卡
     */
    public static final int SCANNER = 5;
    /**
     * 监控卡
     */
    public static final int MONITORING_CARD = 6;
    /**
     * 风扇
     */
    public static final int MONITORING_CARD_FAN = 7;
    /**
     * 电压
     */
    public static final int MONITORING_CARD_VOLTAGE = 8;
    /**
     * 排线
     */
    public static final int MONITORING_CARD_FLAT_CABLE = 9;
    /**
     * 多功能卡
     */
    public static final int FUNCTION_CARD = 10;
    /**
     * 电脑
     */
    public static final int COMPUTER = 11;
    /**
     * 智能模组
     */
    public static final int SMART_MODULE = 12;
    /**
     * 智能模组行
     */
    public static final int SMART_MODULE_ROW = 13;
    /**
     * 智能模组列
     */
    public static final int SMART_MODULE_COL = 14;

    /**
     * 空字符串
     */
    public static final String STRING_EMPTY = "";

    /**
     * 设备工作状态正常
     */
    public static final int DEVICE_WORK_STATUS_OK = 0;
    /**
     * 设备工作状态故障
     */
    public static final int DEVICE_WORK_STATUS_ERROR = 1;
    /**
     * 设备工作状态未知
     */
    public static final int DEVICE_WORK_STATUS_UNKNOWN = 2;

    /**
     * 获取发送卡简短描述信息
     *
     * @param senderMonitorInfo 发送卡
     * @return 简短描述信息
     */
    public static String getSenderCardShortMsg(SenderMonitorInfo senderMonitorInfo) {
        if (senderMonitorInfo == null ||
                senderMonitorInfo.getDevMappingList() == null ||
                senderMonitorInfo.getDevMappingList().size() <= 0 ||
                senderMonitorInfo.getDevMappingList().isEmpty()) {
            return STRING_EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Contacts.getResourceValue("SendCard")).append(":").append(getSenderCardIndex(senderMonitorInfo));
        stringBuilder.append("\r\n");
        stringBuilder.append(Contacts.getResourceValue("WorkStatus")).append(":").append(getDeviceWorkStatus(senderMonitorInfo.getDevWorkStatus()));
        return stringBuilder.toString();
    }

    /**
     * 获取接收卡简短描述信息
     *
     * @param receiveCardInfo 接收卡信息
     * @return 接收卡简短描述信息
     */
    public static String getReceiveCardShortMsg(HardwareDistribution.ScanBoardRegionInfo receiveCardInfo) {
        if (receiveCardInfo == null) {
            return STRING_EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();

        if (receiveCardInfo.senderIndex == -1) {
            stringBuilder.append(Contacts.getResourceValue("ReceiveCard")).append(":").append(Contacts.getResourceValue("ReceiveCardEmpty"));
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("SendCard"));
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("NetPort"));
        } else {
            stringBuilder.append(Contacts.getResourceValue("ReceiveCard")).append(":").append(receiveCardInfo.connectIndex);
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("SendCard")).append(":").append(receiveCardInfo.senderIndex);
            stringBuilder.append("\r\n");
            stringBuilder.append(Contacts.getResourceValue("NetPort")).append(":").append(receiveCardInfo.portIndex);
        }
        stringBuilder.append("\r\n");
        stringBuilder.append("X").append(":").append(receiveCardInfo.X);
        stringBuilder.append("\r\n");
        stringBuilder.append("Y").append(":").append(receiveCardInfo.Y);
        stringBuilder.append("\r\n");
        stringBuilder.append("Width").append(":").append(receiveCardInfo.width);
        stringBuilder.append("\r\n");
        stringBuilder.append("Height").append(":").append(receiveCardInfo.height);
        return stringBuilder.toString();
    }

    /**
     * 获取设备名称
     *
     * @param deviceType 设备类型
     * @return 设备名称
     */
    public static String getDeviceName(int deviceType) {
        String deviceName = STRING_EMPTY;
        switch (deviceType) {
            case UNDEFINED:
                deviceName = Contacts.getResourceValue("Device_UNDEFINED");
                break;
            case LED_SCREEN:
                deviceName = Contacts.getResourceValue("Device_LED_SCREEN");
                break;
            case SEND_CARD:
                deviceName = Contacts.getResourceValue("Device_SEND_CARD");
                break;
            case SEND_CARD_PORT:
                deviceName = Contacts.getResourceValue("Device_SEND_CARD_PORT");
                break;
            case DECONCENT_CARD:
                deviceName = Contacts.getResourceValue("Device_DECONCENT_CARD");
                break;
            case SCANNER:
                deviceName = Contacts.getResourceValue("Device_SCANNER");
                break;
            case MONITORING_CARD:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD");
                break;
            case MONITORING_CARD_FAN:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_FAN");
                break;
            case MONITORING_CARD_VOLTAGE:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_VOLTAGE");
                break;
            case MONITORING_CARD_FLAT_CABLE:
                deviceName = Contacts.getResourceValue("Device_MONITORING_CARD_FLAT_CABLE");
                break;
            case FUNCTION_CARD:
                deviceName = Contacts.getResourceValue("Device_FUNCTION_CARD");
                break;
            case COMPUTER:
                deviceName = Contacts.getResourceValue("Device_COMPUTER");
                break;
            case SMART_MODULE:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE");
                break;
            case SMART_MODULE_ROW:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE_ROW");
                break;
            case SMART_MODULE_COL:
                deviceName = Contacts.getResourceValue("Device_SMART_MODULE_COL");
                break;
            default:
                deviceName = Contacts.getResourceValue("Device_UNDEFINED");
                break;
        }
        return deviceName;
    }

    /**
     * 获取发送卡位置索引
     *
     * @param senderMonitorInfo 发送卡信息
     * @return 发送卡位置索引
     */
    public static int getSenderCardIndex(SenderMonitorInfo senderMonitorInfo) {
        int sendCardIndex = -1;
        if (senderMonitorInfo == null ||
                senderMonitorInfo.getDevMappingList() == null ||
                senderMonitorInfo.getDevMappingList().size() <= 0 ||
                senderMonitorInfo.getDevMappingList().isEmpty()) {
            return sendCardIndex;
        }
        List<DeviceSearchMapping> deviceSearchMappingList = senderMonitorInfo.getDevMappingList();
        for (DeviceSearchMapping deviceSearchMapping : deviceSearchMappingList) {
            if (deviceSearchMapping.getDevType() == SEND_CARD) {
                sendCardIndex = deviceSearchMapping.getDevIndex();
                break;
            }
        }
        return sendCardIndex;
    }

    /**
     * 获取发送卡工作状态描述
     *
     * @param deviceWorkStatus 工作状态枚举
     * @return 工作状态描述
     */
    public static String getDeviceWorkStatus(int deviceWorkStatus) {
        String workStatus = "正常";
        switch (deviceWorkStatus) {
            case 0:
                workStatus = Contacts.getResourceValue("WorkStatusOK");
                break;
            case 1:
                workStatus = Contacts.getResourceValue("WorkStatusError");
                break;
            default:
                workStatus = Contacts.getResourceValue("WorkStatusUnknown");
                break;
        }
        return workStatus;
    }
}
