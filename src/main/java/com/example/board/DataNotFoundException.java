package com.example.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

// HTTP 상태 코드와 메시지를 설정하여 예외 발생 시 해당 상태 코드와 메시지를 반환하도록 지정합니다.
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends RuntimeException {

    // 직렬화 버전 UID로, 클래스의 상태를 직렬화하고 역직렬화할 때 사용됩니다.
    @Serial
    private static final long serialVersionUID = 1L;

    // 예외 발생 시 메시지를 지정하는 생성자입니다.
    public DataNotFoundException(String message) {
        super(message); // 상위 클래스인 RuntimeException의 생성자를 호출하여 메시지를 설정합니다.
    }
}