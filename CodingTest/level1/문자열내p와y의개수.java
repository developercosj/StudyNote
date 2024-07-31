class 문자열내p와y의개수 {
    public static void main(String[] args) {
        solution("psasppyyyy");
    }

    static boolean solution(String s) {
        boolean answer = true;

        String lowerCaseWord = s.toLowerCase();

        int[] p = {0};
        s.toLowerCase().chars().filter(x -> x == 'p').forEach (
                x -> p[0]++
        );
        int[] y = {0};
        s.toLowerCase().chars().filter(x -> x == 'y').forEach (
                x -> y[0]++
        );

        return y[0] == p[0] ;
    }
}