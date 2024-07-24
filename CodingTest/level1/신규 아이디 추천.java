import java.util.*;
class Scratch {
    public static void main(String[] args) {
    }

    static public int solution(String new_id) {
        String answer = "";
        // 1단계
        answer = new_id.toLowerCase();

        // 2단계
        answer = answer.replaceAll("[^a-z0-9-_.]", "");

        // 3단계
        answer = answer.replaceAll("\\.{2,}", ".");

        // 4단계
        answer = answer.replaceAll("^\\.|\\.$", "");


        // 5단계
        if (answer.equals("")) {
            answer = "a";
        }

        // 6단계
        if (answer.length() > 15) {
            answer = answer.substring(0, 15);
        }

        // 6-1 단계
        answer = answer.replaceAll("\\.$", "");

        // 7단계

        if (answer.length() >= 1 && answer.length() < 3) {
            String lastChar = "" + answer.charAt(answer.length() - 1);
            for (int i = answer.length(); i < 3; i++) {
                answer = answer + lastChar;
            }

        }

        return answer;
        }
    }

}