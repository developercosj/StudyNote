class 최솟값만들기퀵정렬 {
    public static void main(String[] args) {
        int a[] = {1, 4, 2};
        int b[] = {5, 4, 4};

        solution(a, b);
    }

    public static int solution(int[] A, int[] B)
    {
        int answer = 0;


        // 첫번째 배열은 오름차순 정렬
        quickSort(A, 0, A.length - 1, true);

        // 두번째 배열은 내림차순
        quickSort(B, 0, B.length - 1, false);


        // 두개의 배열을 순차적으로 곱할것

        for (int i = 0; i < A.length ; i++) {
            answer = answer + A[i] * B[i];

        }

        return answer;
    }


    // 퀵정렬

    // 퀵 정렬 함수
    static void quickSort(int[] array, int low, int high, boolean ascending) {
        if (low < high) {
            // 파티션 인덱스 찾기
            int pi = partition(array, low, high, ascending);

            // 재귀적으로 왼쪽과 오른쪽 부분 배열을 정렬
            quickSort(array, low, pi - 1, ascending);
            quickSort(array, pi + 1, high, ascending);

        }


    }


    // 배열을 피벗을 기준으로 나누고 피벗의 인덱스 반환
    // low 와 high 는 인덱스다.
    static int partition(int[] arr, int low, int high, boolean ascending) {
        int pivot = arr[high];
        int i = (low - 1); // 가장 작은 요소의 인덱스

        for (int j = low; j < high; j++) {
            // 현재 요소가 피벗보다 작거나 같으면
            if ( ascending ? arr[j] <= pivot : arr[j] >= pivot) {
                i++;
                // 배열의 i번째 요소와 현재 요소 교환
                swap(arr, i, j);
            }
        }

        swap(arr, i+1, high);

        return i+1;


    }

    // 배열 두 요소 교환
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}