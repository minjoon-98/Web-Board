package com.example.board.Answer;

import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {
    @NotEmpty(message = "내용은 필수항목입니다.")
    @Pattern(regexp = "^(?=.*\\S).*$", message = "내용을 입력해주세요.")
    private String content;
}