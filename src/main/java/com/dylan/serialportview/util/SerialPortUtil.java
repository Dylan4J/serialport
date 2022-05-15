package com.dylan.serialportview.util;

import cn.hutool.core.io.checksum.crc16.CRC16Modbus;
import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @program: serialport
 * @description: 封装对串口的部分操作
 * @author: Dylan
 * @create: 2022-05-14 16:04
 **/

public class SerialPortUtil {
    private static List<SerialPort> serialPorts;
    private static CRC16Modbus checker;

    static {
        ArrayList<SerialPort> serialPortList = new ArrayList<SerialPort>();
        Collections.addAll(serialPortList, SerialPort.getCommPorts());
        serialPorts = serialPortList;
        checker = new CRC16Modbus();
    }

    /**
     * 需要发送的字符串转换成16进制的字节数组并进行校验
     *
     * @param origin
     * @return
     */
    public static byte[] strToBytesWithCheck(String origin) {
        byte[] bytes = strToBytes(origin);
        //CRC校验
        long checksum = check(bytes);
        byte[] checksumBytes = long2Bytes(checksum);
        //将原始数据与校验和数组组合成新数组，校验和数组追加在原始数组末尾
        byte[] res = Arrays.copyOf(bytes, bytes.length + 2);
        //调换
        res[res.length - 2] = checksumBytes[0];
        res[res.length - 1] = checksumBytes[1];
        return res;
    }

    /**
     * 需要发送的字符串转换成16进制的字节数组并进行校验
     *
     * @param origin
     * @return
     */
    public static byte[] strToBytes(String origin) {
        String[] s = origin.split(" ");
        byte[] bytes = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s[i], 16);
        }
        return bytes;
    }
    /**
     * 接受到的字节数组转换为16进制形式的字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toHexString(Byte.toUnsignedInt(aByte)));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * long类型转换成为byte数组
     *
     * @param number
     * @return
     */
    public static byte[] long2Bytes(long number) {
        byte[] res = new byte[8];
        for (int i = 0; i < res.length; i++) {

            res[i] = (byte) (number >> 8 * i);
        }
        return res;
    }

    /**
     * 获取字节数组的校验和
     *
     * @param bytes
     * @return
     */
    public static long check(byte[] bytes) {
        checker.update(bytes);
        return checker.getValue();
    }

    /**
     * 根据端口名称获取物理端口
     *
     * @param name
     * @return
     */
    public static SerialPort getPortByName(String name) {

        List<SerialPort> portsByName = serialPorts.stream().filter(serialPort -> serialPort.getSystemPortName().equals(name)).collect(Collectors.toList());
        return portsByName.get(0);
    }

    /**
     * 获取所有可用的串口
     *
     * @return
     */
    public static List<SerialPort> getPorts() {
        return getSerialPorts();
    }

    private static List<SerialPort> getSerialPorts() {
        return serialPorts;
    }


}
