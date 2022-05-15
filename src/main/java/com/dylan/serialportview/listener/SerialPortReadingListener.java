package com.dylan.serialportview.listener;


import com.dylan.serialportview.component.WeightParser;
import com.dylan.serialportview.util.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @program: serialport
 * @description: 用于串口读取事件的监听
 * @author: Dylan
 * @create: 2022-05-14 17:32
 **/
@Getter
@Setter
public class SerialPortReadingListener implements SerialPortPacketListener {
    private static volatile SerialPortReadingListener listener = null;
    private SerialPort port = null;
    private WeightParser parser;

    private static final int parseDataLength = 1500;
    /**
     * 触发读取监听器时从串口读取到的内容
     */
    private String context = "";

    private SerialPortReadingListener() {
        this.parser = new WeightParser(parseDataLength);
    }

    public static SerialPortReadingListener getInstance(){
        if (listener == null) {
            synchronized (SerialPortReadingListener.class){
                if (listener == null){
                    listener = new SerialPortReadingListener();
                }
            }
        }
        return listener;
    }


    @Override
    public int getListeningEvents() {
        // TODO Auto-generated method stub
        // 返回要监听的事件类型，以供回调函数使用。可发回的事件包括：
        // SerialPort.LISTENING_EVENT_DATA_AVAILABLE，
        // SerialPort.LISTENING_EVENT_DATA_WRITTEN,
        // SerialPort.LISTENING_EVENT_DATA_RECEIVED。
        // 分别对应有数据在串口（不论是读的还是写的），有数据写入串口，从串口读取数据。如果AVAILABLE和RECEIVED同时被监听，优先触发RECEIVED
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        // TODO Auto-generated method stub
        String data = "";
        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            byte[] receivedDataBytes = event.getReceivedData();
            context = SerialPortUtil.bytesToStr(receivedDataBytes);
            System.out.println("共读取了 " + receivedDataBytes.length + " 字节.");
            System.out.println("读取的内容为 ： " + context);
            long weight = parseDataOfWight(receivedDataBytes);
            synchronized (WeightParser.class){
                parser.add(weight);
            }
            System.out.println("称重数据为：" + weight + " g");

        }

    }

    /**
     * 解析收到的字节数组，将其转变为重量值
     *
     * @param bytes
     * @return
     */
    public long parseDataOfWight(byte[] bytes) {
        long base = 0;
        long res = 0;
        boolean flag = true;
        for (int i = 0; i < 4; i++) {
            byte b = bytes[i + 3];
            long temp = 0;
            if (i == 0 && b < 0) {
                flag = false;
            }
            temp = Byte.toUnsignedLong(b);
            res = res | temp << ((3 - i) * 8);

        }
        if (!flag) {
            //如果重量值为负数，则求其负向力的大小
            long unsignedIntegerMaxVal = ((long) Integer.MAX_VALUE << 1) + 1;
            res = res - unsignedIntegerMaxVal;

        }
        return res;
    }

    @Override
    public int getPacketSize() {
        return 9;
    }


}
