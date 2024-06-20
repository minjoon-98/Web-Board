package com.example.board.Answer;

import com.example.board.Question.Question;
import com.example.board.Question.QuestionService;
import com.example.board.user.SiteUser;
import com.example.board.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


// "/answer" 경로에 대한 요청을 처리하는 컨트롤러로 설정합니다.
@RequestMapping("/answer")
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Controller // 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
public class AnswerController {

    // 의존성 주입을 위한 서비스 클래스들
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}") // POST 요청을 처리하며, URL 경로의 {id} 변수를 매핑합니다.
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm,
                               BindingResult bindingResult, Principal principal) {
        // 질문 ID에 해당하는 질문 객체를 가져옵니다.
        Question question = this.questionService.getQuestion(id);
        // 현재 로그인한 사용자 정보를 가져옵니다.
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // 폼 검증에서 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question); // 모델에 질문 정보를 추가합니다.
            return "question_detail"; // 폼 오류 시 질문 상세 페이지로 돌아갑니다.
        }
        // 답변을 생성합니다.
        this.answerService.create(question, answerForm.getContent(), siteUser);
        // 질문 상세 페이지로 리다이렉트합니다.
        return String.format("redirect:/question/detail/%s", id);
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}") // GET 요청을 처리하며, URL 경로의 {id} 변수를 매핑합니다.
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        // 답변 ID에 해당하는 답변 객체를 가져옵니다.
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인합니다.
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            // 작성자가 아닌 경우 예외를 발생시킵니다.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 폼에 기존 답변 내용을 설정합니다.
        answerForm.setContent(answer.getContent());
        // 답변 수정 폼 페이지를 반환합니다.
        return "answer_form";
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}") // POST 요청을 처리하며, URL 경로의 {id} 변수를 매핑합니다.
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        // 폼 검증에서 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            return "answer_form"; // 폼 오류 시 답변 수정 페이지로 돌아갑니다.
        }
        // 답변 ID에 해당하는 답변 객체를 가져옵니다.
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인합니다.
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            // 작성자가 아닌 경우 예외를 발생시킵니다.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        // 답변 내용을 수정합니다.
        this.answerService.modify(answer, answerForm.getContent());
        // 수정된 답변의 질문 상세 페이지로 리다이렉트합니다.
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}") // GET 요청을 처리하며, URL 경로의 {id} 변수를 매핑합니다.
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        // 답변 ID에 해당하는 답변 객체를 가져옵니다.
        Answer answer = this.answerService.getAnswer(id);
        // 현재 사용자가 답변의 작성자인지 확인합니다.
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            // 작성자가 아닌 경우 예외를 발생시킵니다.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        // 답변을 삭제합니다.
        this.answerService.delete(answer);
        // 삭제된 답변의 질문 상세 페이지로 리다이렉트합니다.
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
}