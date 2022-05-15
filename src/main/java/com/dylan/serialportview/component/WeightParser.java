package com.dylan.serialport.component;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @program: serialport
 * @description: 对重量进行记录并分析
 * @author: Dylan
 * @create: 2022-05-14 22:42
 **/
@Getter
public class WeightParser {
    private Deque<Long> weightData;

    public WeightParser(int iniDataLength) {
        this.weightData = new ArrayDeque<>(iniDataLength);
    }

    /**
     * 添加数据
     * @param singleWeight
     */
    public void add(long singleWeight){
        if (!weightData.offer(singleWeight)){
            weightData.poll();
            weightData.offer(singleWeight);
        }
    }


}
