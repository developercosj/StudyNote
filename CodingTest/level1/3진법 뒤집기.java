class Scratch {
    public static void main(String[] args) {
        
    }


    static public int solution(Integer n) {

        
        // 정수를 3진법으로 변환
        String str = Integer.toString(n, 3);
        String reversed = new StringBuilder(str).reverse().toString();
        return Integer.valueOf(reversed, 3);

    }

}