import java.util.*;

class Scratch {
    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        int a = stdIn.nextInt();
        int b = stdIn.nextInt();
        int c = stdIn.nextInt();
        System.out.println(med3(a, b, c));
    }
    static int med3(int a, int b, int c) {
        
        if (a > b) {
            
            if (a > c) {
                if (b >= c) {
                    return b;
                } else {
                    return c; 
                }
                
            } else if (a <= c) {
                return a ;
            }
            
        } else if (a == b) {
            if (a < c) {
                return a;
            } else {
                return c; 
            }
        } else {
            
            if (b > c) {
                if (c < a) {
                    return a;
                } else {
                    return c; 
                }
            } else {
                return b; 
            }
        }
        return -1;
    }


    static int med3new(int a, int b, int c) {

        if (a <= b) {
            if (c <= a) {
                return a ;
            } else if ( b >= c) {
                return c;
            } else {
                return b;
            }
        } else if (a < c) { // a > b
            return a;
        } else if (b > c) { // a > b  a >= c
            return b;
        } else {
            return c;
        }
        
    }
}