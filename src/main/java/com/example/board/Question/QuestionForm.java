package com.example.board.Question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Pattern(regexp = "^(?=.*\\S).*$", message = "제목을 입력해주세요.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    @Pattern(regexp = "^(?=.*\\S).*$", message = "내용을 입력해주세요.")
    private String content;
}