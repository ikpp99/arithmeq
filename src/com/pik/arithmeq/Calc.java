package com.pik.arithmeq;
import java.util.ArrayList;
import java.util.List;
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
            if( ((String)fun).equals("qqqqq") ){
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

//      String eqv="-1.98^3+(1.23e-2)^( 7/1.45 ) -A + B/C * D^E - ( -F+ G* fun( H, I- J ^ (K-1), L ) + M ) -O/ P*Q^R+ S -(1.23e-2)^( 7 )";
//      String eqv="-A+123.34e-5*(-C+D/E^(-3.22))";
//      String eqv="-( A + B*C^3/E + Fun( QQQ, RRR, SSS ))";
        String eqv="-( A + 123.456e-22*C^(-3.111)/E + Fun( QQQ, RRR-S/T, UUU )*G )";

        tt( eqv );
        tt("Compile:\n"+ q.compile( eqv )+"\n");
        
        Double         A=111.,B=222., C=777., E=2., F=7.;
        Object[] prog={A,"_", B,      C,"/",  E,    F, "^",     "*", "+"};
        Object[] pro2={A,"_", B,      C,"/",  E,    F, "qqqqq", "*", "+"};  //### dbg: fun(a,b) = qqqqq = pow
        
        tt("@@@ res = "+q.execute( prog )
        +"\n direct = "+(-A+B/C*Math.pow( E,F ))
        +"\n symb_^ = "+q.execute( prog )
        +"\n symb_q = "+q.execute( pro2 )
        );
    }
//---------------------------------------------------------------------------------- Compile:
    private ValFun ext;
    private Stack<Double> stack;
    private Stack<String> funStack;

    private ArrayList<String> tokens; int t=0, tx=-1;
    private ArrayList<Object> progs;  int p=0; 

    public Calc( ValFun extention ){ ext=extention;}

    public List<Object> compile( String eqv )
    {
        eqv = eqv.replaceAll("\\s","");
        tokens = new ArrayList<>();
        StringTokenizer st = new StringTokenizer( eqv,"+-*/^(),", true );
        while (st.hasMoreTokens()) tokens.add( st.nextToken() );
        tt(tokens.size()+" @@@ TokenList:\n"+tokens);
        
        tx = tokens.size(); if( tx < 1 ) return null;
        progs = new ArrayList<>(); p=t=0;
        funStack = new Stack<>();
        
        try{ parsEqv( false );}
        catch( Exception ex ) {
            ex.printStackTrace();
            System.out.println("Eqv.: "+eqv+"\nToks: "+tokens+"\nProg: "+progs);
            progs=null;
        }
        funStack = null; 
        tokens   = null;
        return progs;
    }
    
    private void parsEqv( boolean fun ) throws Exception 
    {
        int funsp=funStack.size(), progp=progs.size();
        while( t < tx ) {
            String  tok = tokens.get( t++ );
            switch( tok ) 
            {
                case "(":   parsEqv( false );  break;
                case ")":   flushFunStack( funsp );  return;

                case ",":   if( !fun ) throw new Exception("Calc.Compile: Incorrect use ','");
                            flushFunStack( funsp );
                            parsEqv( true );  return;
                            
                case "+":
                case "-":   if( funStack.size() ==funsp && progs.size() ==progp ){  // unar.MINUS
                                if( tok.equals("-")) funStack.push("_");  break;
                            }
                case "*":   
                case "/":
                case "^":   popAct( tok, funsp );  break;

                default:    if( cifra( tok.charAt( 0 ))){                           // Constant
                    String cons = tok;
                    if( tok.substring( tok.length()-1 ).equalsIgnoreCase("E") && t+1 <tx ) {        
                        cons += tokens.get( t ) + tokens.get( t+1 );
                        t+=2;
                    }
                    progs.add( Double.parseDouble( cons )); 
                } 
                else {
                    if( t<tx && tokens.get( t ).equals("(")){                       // Fun() 
                        t++; popAct( tok, funsp );
                        parsEqv( true );
                    } 
                    else progs.add( "@"+tok );                                      // @Variable
                }
            }
        }
        flushFunStack( funsp );
    }
    private void popAct( String tok, int funsp ) {
        int rank = rankAct( tok );
        while( funStack.size() > funsp && rank < rankAct( funStack.peek())) progs.add( funStack.pop() );
        funStack.push( tok );
    }
    private int rankAct( String tok ) {
        switch( tok.charAt( 0 )) {
            case '+':
            case '-':  return 0;
            case '*':
            case '/':  return 1;
            case '_':  return 3;  //unar.MINUS
        }
        return 2;
    }
    private void flushFunStack( int funsp ) {
        while( funStack.size() > funsp ) progs.add( funStack.pop());
    }
    private boolean cifra( char c ){ return ('0'<=c && c<='9') || c=='.';}
//---------------------------------------------------------------------------------- Execute:
/*
 * @parameter List<Object> prog - command sequence to the StackProcessor: Double, "_-/+*^", "Fun()" 
 *   contains: Double, "_"-unar.MINUS, "+/-*^", "Fun".
 */
    public void   push( Double v ){ stack.push( v );}
    public Double pop(){ return stack.pop();}

    public Object execute( Object[] prog ) throws Exception {
        if( prog ==null || prog.length <1 ) return null;
        stack = new Stack<Double>();
        for( int p=0;p<prog.length;p++)
        {
            Object   pro = prog[ p ];
            if(      pro instanceof Double  ) stack.push( (Double)pro );
            else if( pro instanceof String  ) //"_+-*/^fun", "@param"
            {    
                String cmd = pro.toString();
                if( cmd.charAt( 0 )=='@') {
                    Double v = ext.getVal( cmd.substring( 1 ), this );
                    if( v==null ) throw new Exception("Calc.Exec: Undefined Value: "+cmd );
                    stack.push( v );
                } 
                else exeFun( cmd );    
            }
        }
        if( stack.size() !=1 ) throw new Exception("Calc.Exec: Incorrect Structure:\nprog: "+prog);
        return stack.pop();
    }
    private void exeFun( String fun ) throws Exception 
    { 
        Double res = (Double)stack.pop();  // in fun( A, B, C ), Last arg.= C
        switch( fun.charAt( 0 ) ){
                case '_':   res =-res;                          break; // -A  
                case '+':   res = stack.pop() + res;            break; // A + B
                case '-':   res = stack.pop() - res;            break; // A - B
                case '*':   res = stack.pop() * res;            break; // A * B
                case '/':   res = stack.pop() / res;            break; // A / B
                case '^':   res = Math.pow( stack.pop(), res ); break; // A ^ B
        default:
                res = ext.exeFun( fun, res, this );
                if( res==null ) throw new Exception("Calc.Exec: Unknown Function: "+fun);
        }
        stack.push( res );
    }
}
