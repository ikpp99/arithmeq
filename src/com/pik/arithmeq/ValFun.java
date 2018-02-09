package com.pik.arithmeq;

public interface ValFun
{
    default Double getVal( Object var, Calc c ){
        return null;
    }
    default Double exeFun( Object fun, Double lastArg, Calc c ){
        return null;
    }
}
