// 패턴에 대한 예시 작성
// Transfer Object 패턴 : Value Object 라고도 함 데이터를 전송하기 위한 객체에 대한 패턴
// 필드를 private 으로 지정해서 getter(), setter() 메서드를 통해서만 값을 만드는 것은 성능상 느려질 수 있으나 정보를 은닉하여 모든 필드의 값을
// 아무나 마음대로 수정을 못하도록 하는 방법을 사용할 수 있다.
// getExamName() 처럼 examName 값이 null 값일 때 null 리턴을 하지 않고 길이가 0인 String 값 리턴하기 때문에 null 체크가 필요없어진다.

public class TransferObject implements Serializable{

    private String examName;
    private String examId;
    private String examAddress;

    public ExamTo() {
        super();
    }

    public TransferObject(String examName, String examId, String examAddress) {
        super();
        this.examName = examName;
        this.examId = examId;
        this.examAddress = examAddress;
    }


    public String getExamName() {
        return examName;
    }

    public String setExamName(String examName) {
        this.examName = examName;
    }


    public String getExamId() {
        return examId;
    }

    public String setExamId(String examId) {
        this.examId = examId;
    }


    public String getExamAddress() {
        return examAddress;
    }

    public String setExamAddress(String examAddress) {
        this.examAddress = examAddress;
    }


    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("examName=").append(examName).append("examId=").append(examId).append("examAddress=").append(examAddress);
        return sb.toString();

    }










}