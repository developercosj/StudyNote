class Scratch {
    public static void main(String[] args) {
        mySolution("solalis apple banana meal breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast breakfast ");
    }
        static public String mySolution(String s) {
            String answer = "";

            String array[] = s.split(" ");

            for(int j = 0; j < array.length; j++) {
                var newWord = "";
                for (int i = 0; i < array[j].length(); i++  ) {

                    if (i == 0 || i % 2 == 0 ) {
                        newWord += Character.toUpperCase(array[j].charAt(i));

                    } else {
                        newWord += Character.toLowerCase(array[j].charAt(i));
                    }

                }
                array[j] = newWord;

            }

            for (int i = 0; i < array.length; i++) {
                answer += array[i];

                if ( i != array.length - 1) {
                    answer += " ";
                }


            }
            System.out.println(answer);
            return answer;
        }

        static public String firstSolution(String s) {

        // 문자열의 모든 문자에 대해서 반복한다.
            boolean toUpper = true;

            StringBuilder builder = new StringBuilder();
            for (char c: s.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    builder.append(c);
                    toUpper = true;
                } else {
                    if (toUpper) {
                        builder.append(Character.toUpperCase(c));
                    } else {
                        builder.append(Character.toLowerCase(c));
                    }

                    toUpper = !toUpper;
                }


            }


        return builder.toString();
        }


}


// 자바의 Character 클래스에서 문자가 알파벳인지 검사 : isAlphabetic()
// 문자가 공백인지 검사 : isSpaceChar()

