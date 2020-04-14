package com.dragon.myaptproject_01;

import com.dragon.apt.annotation.Factory;

@Factory(name = "add", type = Calculate.class)
public class AddCalculate implements Calculate {

    @Override
    public int operation(int a, int b) {
        return a + b;
    }

}
