package com.dragon.myaptproject_01;


import com.dragon.apt.annotation.Factory;

@Factory(name = "cut", type = Calculate.class)
public class CutCalculate implements Calculate {
    @Override
    public int operation(int a, int b) {
        return a - b;
    }
}
