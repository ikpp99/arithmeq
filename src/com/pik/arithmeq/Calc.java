package com.pik.arithmeq;
/*
 * Arithmetic Equation Calulator
 * @author k.ilyashenko, 07.02.2018
 */
import java.util.Stack;

public class Calc
{
    static class valfun implements ValFun {  //dbg

        @Override public Object exeFun( Object fun, Calc c ){
            if( ((String)fun).equals("(2qqqqq") ){
                Double y=(Double)c.pop();
                return Math.pow( (Double)c.pop(), y );
            }
            return null;
        }
    } 

    static void tt(String x){ System.out.println( x );}
    public static void main(String[] a) throws Exception 
    {
//      Calc q = new Calc( null );
        Calc q = new Calc( new Calc.valfun());  //dbg
    
        tt("(2^ = "+ "(2^".hashCode() );
        
        Double         A=111., B=222., C=777., E=2., F=7.;
        Object[] prog={A,"(1-",B,      C,"(2/",E,    F, "(2^","(2*","(2+"};
        Object[] pro2={"#111","(1-","#222","#777","(2/","#2","#7","(2qqqqq","(2*","(2+"};
        
        tt("@@@ res = "+q.execute( prog )
        +"\n direct = "+(-A+B/C*Math.pow( E,F ))
        +"\n symbol = "+q.execute( pro2 )
        );
    }
//---------------------------------------------------------------------------------- implementation:
    
    private ValFun ext;
    private Stack<Object> stack;
    
    
    public Calc( ValFun extention ){ ext=extention;}
    
    public Object[] compile( String eqv ){
        return null;
    }
/*
 * @parameter Object[] prog - sequence of the commands to the StackProcessor
 * examples:
 *           values:     "@param", "#1234.56e-11", Double: value;
 *           functions:  "(2fun", Integer: <Fun.hashCode>
 */
    public Object execute( Object[] prog ) throws Exception {
        if( prog ==null || prog.length <1 ) return null;
        stack = new Stack<Object>();
        for( int p=0;p<prog.length;p++)
        {
            Object pro = prog[ p ];
            if( pro instanceof Double ) stack.push( pro );
        
            else if( pro instanceof Integer ) exeFun( (Integer)pro );
            
            else if( pro instanceof String  )               //""(2fun", "#1234.56e-11", "@param"
            {    
                String  val = pro.toString();
                switch( val.charAt( 0 ))
                {
                    case '(': exeFun( val ); break;
                    
                    case '#': stack.push( new Double( val.substring( 1 ))); break;
                    
                    case '@': pro = ext.getVal( val.substring( 1 ), this );
                              if( pro==null ) throw new Exception("Undefined Value: "+val );
                              stack.push( pro ); break;
                    
                    default: throw new Exception("Unsupported Operand: "+val );
                }
            }
        }
        if( stack.size() != 1 ){
            String s="eqv: "; for(Object q: prog ) s+=q.toString()+"|";
            throw new Exception("Incorrect Equation Structure:\n"+s);
        }
        return stack.pop();
    }
    
    private void exeFun( Object fun ) throws Exception {  // "(2fun", Integer: <Fun.hashCode>
        int code = fun instanceof String? fun.toString().hashCode(): ((Integer)fun).intValue();
        Double res = (Double)stack.pop();  // in fun( A, B, C ), Last arg.= C
        
        switch( code ){
            case 40002: //"(1+"
                        break;
            case 40004: //"(1-"
                        res = -res; break;
            case 40033: //"(2+"
                        res = (Double)stack.pop() + res; break;
            case 40035: //"(2-"
                        res = (Double)stack.pop() - res; break;
            case 40032: //"(2*"
                        res = (Double)stack.pop() * res; break;
            case 40037: //"(2/"
                        res = (Double)stack.pop() / res; break;
            case 40084: //"(2^"
                        res = Math.pow( (Double)stack.pop(), res ); break;
        default:
            stack.push( res );
            res = (Double) ext.exeFun( fun, this );
            if( res==null ) throw new Exception("Unknown Function: "+fun);
        }
        stack.push( res );
    }

    public void   push( Object v ){ stack.push( v );}
    public Object pop(){ return stack.pop();}
}
