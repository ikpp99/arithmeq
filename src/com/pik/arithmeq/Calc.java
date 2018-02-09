package com.pik.arithmeq;
import java.util.ArrayList;
/*
 * Arithmetic Equation Calulator
 * @author k.ilyashenko, 07.02.2018
 */
import java.util.Stack;
import java.util.StringTokenizer;

public class Calc
{
    static class valfun implements ValFun {  //dbg

        @Override public Double exeFun( Object fun, Double arg, Calc c ){
            if( ((String)fun).equals("(2qqqqq") ){
                return Math.pow( c.pop(), arg );
            }
            return null;
        }
    } 

    static void tt(String x){ System.out.println( x );}
    public static void main(String[] a) throws Exception 
    {
//      Calc q = new Calc( null );
        Calc q = new Calc( new Calc.valfun());  //dbg
        
        String eqv="-1.98^3+(1.23e-2)^( 7/1.45 ) -A + B/C * D^E - ( -F+ G* fun( H, I- J ^ (K-1), L ) + M ) -O/ P*Q^R+ S -(1.23e-2)^( 7 )";
        
        Object[] ppp = q.compile( eqv );
    
        tt("(2^ = "+ "(2^".hashCode() );
        
        Double         A=111., B=222., C=777., E=2., F=7.;
        Object[] prog={A,"(1-",B,      C,"(2/",E,    F, "(2^","(2*","(2+"};
        Object[] pro2={"#111","(1-","#222","#777","(2/","#2","#7","(2qqqqq","(2*","(2+"}; //### dbg: fun(a,b) = qqqqq = pow
        
        tt("@@@ res = "+q.execute( prog )
        +"\n direct = "+(-A+B/C*Math.pow( E,F ))
        +"\n symbol = "+q.execute( pro2 )
        );
    }
//---------------------------------------------------------------------------------- Compile:
    private ValFun ext;
    private Stack<Double> stack;

    public Calc( ValFun extention ){ ext=extention;}

    public Object[] compile( String eqv )
    {
        eqv = eqv.replaceAll("\\s","");
        tokens = new ArrayList<>();
        StringTokenizer st = new StringTokenizer( eqv,"+-*/^(),", true );
        while (st.hasMoreTokens()) tokens.add( st.nextToken() );          tt(tokens.size()+" @@@ TokenList:\n"+tokens);
        
        tx = tokens.size(); if( tx < 1 ) return null;
        progs = new ArrayList<>(); p=t=skob=0;
        funStack = new Stack<>();
        
        return null;
    }
    
    private ArrayList<String> tokens; int t=0, tx=-1, skob=0;
    private ArrayList<Object> progs;  int p=0; 
    private Stack<String> funStack;
//---------------------------------------------------------------------------------- Execute:
/*
 * @parameter Object[] prog - sequence of the commands to the StackProcessor
 * examples:
 *           values:     "@param", "#1234.56e-11", Double: value;
 *           functions:  "(2fun", Integer: <Fun.hashCode>
 */
    public Object execute( Object[] prog ) throws Exception {
        if( prog ==null || prog.length <1 ) return null;
        stack = new Stack<Double>();
        for( int p=0;p<prog.length;p++)
        {
            Object pro = prog[ p ];
            if( pro instanceof Double ) stack.push( (Double)pro );
        
            else if( pro instanceof Integer ) exeFun( (Integer)pro );
            
            else if( pro instanceof String  )               //""(2fun", "#1234.56e-11", "@param"
            {    
                String  val = pro.toString();
                switch( val.charAt( 0 ))
                {
                    case '(': exeFun( val ); break;
                    
                    case '#': stack.push( new Double(val.substring( 1 ))); break;
                    
                    case '@': Double v = ext.getVal( val.substring( 1 ), this );
                              if( v==null ) throw new Exception("Undefined Value: "+val );
                              stack.push( v ); break;
                    
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
        
        switch( code )
        {
            case 40002:                                     break;  //"(1+"  - unar.plus
                        
            case 40004: res = -res;                         break;  //"(1-"  - unar.minus
            
            case 40033: res = stack.pop() + res;            break;  //"(2+"  - A + B
            
            case 40035: res = stack.pop() - res;            break;  //"(2-"  - A - B
            
            case 40032: res = stack.pop() * res;            break;  //"(2*"  - A * B
            
            case 40037: res = stack.pop() / res;            break;  //"(2/"  - A / B
            
            case 40084: res = Math.pow( stack.pop(), res ); break;  //"(2^"  - A ^ B
            
        default:
            res = (Double) ext.exeFun( fun, res, this );
            if( res==null ) throw new Exception("Unknown Function: "+fun);
        }
        stack.push( res );
    }

    public void   push( Double v ){ stack.push( v );}
    public Double pop(){ return stack.pop();}
}
