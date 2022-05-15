package com.dylan.serialport.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * @program: serialport
 * @description: 用于监听写入串口成功
 * @author: Dylan
 * @create: 2022-05-14 17:50
 **/
public class SerialPortWritingListener implements SerialPortDataListener {
    private SerialPort port = null;


    public SerialPortWritingListener(SerialPort port) {
        this.port = port;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN) {
            System.out.println("串口(" + port.getSystemPortName() + ") :" + "本次数据传输成功");
        }

    }
}
