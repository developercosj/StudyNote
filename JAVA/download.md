백엔드 파일 다운로드 기능 구현 




# OutputStream   
- 서버에서 파일 내용을 클라이언트로 전송하기 위해 사용하는 스트림
- JAVA/Kotlin 의 기본 I/O 클래스
- 클라이언트로 데이터를 보낼 때 response.outputStream.write() 사용 
- 파일, PDF, DOCX, 이미지 등 다양한 바이너리 데이터를 전달할 때 사용한다. 
- 예시) 

```val outputStream = response.outputStream
outputStream.write(fileBytes)
outputStream.flush()
outputStream.close()
```

# Content-Disposition 헤더 설정 
- 브라우저나 클라이언트가 파일로 저장하도록 힌트를 주는 HTTP 응답 헤더 
- attachment 로 설정하면 다운로드 창이 뜸
- filename="..." 을 지정하면 기본 파일명 설정 가능 
- 예시)
``` 예시
 response.setHeader("Content-Disposition", "attachment; filename=\"chat_history.${downloadExtension.name.lowercase()}\"")
```

# Content-Type 헤더 설정 
- MIME 타입 
- 파일 형식을 설명하는 HTTP 헤더
- 브라우저나 포스트맨이 파일 내용을 어떻게 해석할지 결정하는 기준 
- 예시)
  PDF	application/pdf
  DOCX	application/vnd.openxmlformats-officedocument.wordprocessingml.document
  TXT	text/plain
  JSON	application/json
- 코드
``` response.contentType = "application/pdf" ```


# File, ByteArray, InputStream 
- 실제로 파일을 전송할 땐 ByteArray, InputStream, File 등을 사용해 데이터를 OutputStream 에 쓴다. 
- 예시 )
``` val file = File("report.pdf")
file.inputStream().use { input ->
    input.copyTo(response.outputStream)
} 
```