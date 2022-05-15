package com.dylan.serialportview;

import cn.hutool.core.util.ObjectUtil;
import com.dylan.serialportview.listener.SerialPortReadingListener;
import com.dylan.serialportview.util.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Deque;
import java.util.List;

/**
 * @program: serialport
 * @description:
 * @author: Dylan
 * @create: 2022-05-15 14:03
 **/
public class Task implements Runnable{
    @Override
    public void run() {
        //查找所有串口
        List<SerialPort> serialPorts = SerialPortUtil.getPorts();
        System.out.println("已发现的接口列表如下：\n--------------------------------");
        for (SerialPort port : serialPorts) {
            //打印串口名称，如COM4
            System.out.println("Port:" + port.getSystemPortName());
            //打印串口类型，如USB Serial
            System.out.println("PortDesc:" + port.getPortDescription());
            //打印串口的完整类型，如USB-SERIAL CH340(COM4)
            System.out.println("PortDesc:" + port.getDescriptivePortName());
            System.out.println("--------------------------------");

        }
        SerialPort com3 = SerialPortUtil.getPortByName("COM3");
        if (ObjectUtil.isNull(com3)) {
            throw new RuntimeException("未找到对应的串口");
        }

        /*
        一次性设置所有的串口参数，第一个参数为波特率，默认9600；
        第二个参数为每一位的大小，默认8，可以输入5到8之间的值；
        第三个参数为停止位大小，只接受内置常量，可以选择(ONE_STOP_BIT, ONE_POINT_FIVE_STOP_BITS, TWO_STOP_BITS)；
        第四位为校验位，同样只接受内置常量，可以选择 NO_PARITY, EVEN_PARITY, ODD_PARITY, MARK_PARITY,SPACE_PARITY。
        */
        com3.setComPortParameters(19200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        if (!com3.isOpen()) {
            //判断串口是否打开，如果没打开，就打开串口。打开串口的函数会返回一个boolean值，用于表明串口是否成功打开了
            boolean isCommOpened = com3.openPort();
            if (isCommOpened) {
                System.out.println("串口 " + com3.getSystemPortName() + " 正常打开");
            } else {
                System.out.println("串口 " + com3.getSystemPortName() + " 打开失败");
            }
        }
        SerialPortReadingListener readingListener = SerialPortReadingListener.getInstance();
        readingListener.setPort(com3);
        com3.addDataListener(readingListener);
        if (com3.isOpen()) {
            //要发送的字符串
            String writeData = "01 03 9C 40 00 02";
//            String writeData = "01 10 9C 5C 00 02 00 04 00 00 00 01";
            byte[] bytes = SerialPortUtil.strToBytesWithCheck(writeData);
            System.out.println("发送内容为："+SerialPortUtil.bytesToStr(bytes));
            //读取间隔时间
            int stepTime = 10;
            //数据记录总时间
            int recordTime = 60;
            //读取数据的次数
            int recordNums = recordTime * 1000 / 15;
            //将字节数组全部写入串口
            for (int i = 0; i < recordNums; i++) {
                com3.writeBytes(bytes, bytes.length);
                //休眠0.1秒，等待下位机返回数据。如果不休眠直接读取，有可能无法成功读到数据
                try {
                    Thread.sleep(stepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Deque<Long> weightData = readingListener.getParser().getWeightData();
            System.out.println("记录完毕");


        }
    }
}
