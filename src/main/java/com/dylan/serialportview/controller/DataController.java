package com.dylan.serialportview.controller;

import com.dylan.serialportview.listener.SerialPortReadingListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Deque;
import java.util.Random;

/**
 * @program: serialport
 * @description:
 * @author: Dylan
 * @create: 2022-05-15 14:34
 **/
@Controller
public class DataController {
    @RequestMapping("/data")
    public String data(){
        return "line-smooth";
    }

    @RequestMapping("/data2")
    public String data2(){
        return "line-smooth2";
    }

    @RequestMapping("/getData")
    @ResponseBody
    public Deque getdata(){
        SerialPortReadingListener listener = SerialPortReadingListener.getInstance();
        Deque<Long> weightData = listener.getParser().getWeightData();
        for (Long e : weightData) {
            if (e == null){
                e = new Long(0);
            }
        }
        return weightData;
    }

    @RequestMapping("/getData2")
    @ResponseBody
    public int[] getdata2(){
        int[] nums = new int[7];
        for (int i = 0; i < nums.length; i++) {
            Random random = new Random();
            nums[i] = random.nextInt(10);
        }
        return nums;
    }


}
