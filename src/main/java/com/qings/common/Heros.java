package com.qings.common;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by qings on 2017/7/18.
 */
public class Heros {

    public static List<String> heros = Lists.newArrayList();

    static {
        heros.add("花木兰");
        heros.add("凯");
        heros.add("菜文姬");
    }
}
