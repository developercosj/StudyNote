class 최솟값만들기버블정렬 {
    public static void main(String[] args) {
        int []A = {1,4,2};
        int []B = {5,4,4};

        solution(A, B);
    }
    public static int solution(int[] A, int[] B)
    {
        int answer = 0;

        // 첫번째 배열은 오름차순 정렬 (버블정렬 연습)
        for (int i = 0; i < A.length - 1; i++) {
            for (int j = 0; j < A.length - 1 - i; j++) {
                if (A[j] > A[j + 1]) {
                    int temp = A[j];
                    A[j] = A[j + 1];
                    A[j + 1] = temp;

                }

            }

        }

        // 두번째 배열은 내림차순
        for (int i = 0; i < B.length - 1 ; i++) {
            for (int j = 0; j < B.length -1 - i; j++) {
                if (B[j] < B[j + 1]) {
                    int temp = B[j];
                    B[j] = B[j + 1];
                    B[j + 1] = temp;

                }

            }

        }

        // 두개의 배열을 순차적으로 곱할것

        for (int i = 0; i < A.length ; i++) {
            answer = answer + A[i] * B[i];

        }


        return answer;
    }

}
