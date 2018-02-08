package com.pik.arithmeq;

public interface ValFun
{
    default Object getVal( Object var, Calc c ){
        return null;
    }
    default Object exeFun( Object fun, Calc c ){
        return null;
    }
}
