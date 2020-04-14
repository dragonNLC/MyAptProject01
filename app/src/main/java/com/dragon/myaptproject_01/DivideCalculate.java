package com.dragon.myaptproject_01;


import com.dragon.apt.annotation.Factory;

@Factory(name = "divide", type = Calculate.class)
public class DivideCalculate implements Calculate {
    @Override
    public int operation(int a, int b) throws ArithmeticException{
        return a / b;
    }
}
