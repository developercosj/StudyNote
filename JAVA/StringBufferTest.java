//CharSequence를 파라미터로 가지는 생성자 테스트
class StringBufferTest {
    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        sb.append("SBTEST");
        StringBufferTest sbt = new StringBufferTest();
        sbt.check(sb);
    }

    public void check(CharSequence cs) {
        StringBuffer sb = new StringBuffer(cs);
        System.out.println("sb.length = " + sb.length());

    }



}