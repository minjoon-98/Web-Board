package com.example.board.Question;

import java.security.Principal;
import java.util.List;

import com.example.board.Answer.AnswerForm;
import com.example.board.user.SiteUser;
import com.example.board.user.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;


// "/question" 경로에 대한 요청을 처리하는 컨트롤러로 설정합니다.
@RequestMapping("/question")
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Controller // 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
public class QuestionController {

    // 의존성 주입을 위한 서비스 클래스들
    private final QuestionService questionService;
    private final UserService userService;

    // 질문 목록을 보여주는 메서드
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page) {
        Page<Question> paging = this.questionService.getList(page); // 페이지 번호에 따른 질문 목록을 가져옵니다.
        model.addAttribute("paging", paging); // 모델에 질문 목록을 추가합니다.
        return "question_list"; // 질문 목록 페이지를 반환합니다.
    }

    // 질문 상세 정보를 보여주는 메서드
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id); // ID에 해당하는 질문을 가져옵니다.
        model.addAttribute("question", question); // 모델에 질문 정보를 추가합니다.
        return "question_detail"; // 질문 상세 페이지를 반환합니다.
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form"; // 질문 생성 폼 페이지를 반환합니다.
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form"; // 폼 검증에서 오류가 발생한 경우 질문 생성 페이지로 돌아갑니다.
        }
        SiteUser siteUser = this.userService.getUser(principal.getName()); // 현재 로그인한 사용자 정보를 가져옵니다.
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser); // 질문을 생성합니다.
        return "redirect:/question/list"; // 질문 목록 페이지로 리다이렉트합니다.
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id); // ID에 해당하는 질문을 가져옵니다.
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다."); // 작성자가 아닌 경우 예외를 던집니다.
        }
        questionForm.setSubject(question.getSubject()); // 폼에 기존 질문 제목을 설정합니다.
        questionForm.setContent(question.getContent()); // 폼에 기존 질문 내용을 설정합니다.
        return "question_form"; // 질문 수정 폼 페이지를 반환합니다.
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form"; // 폼 검증에서 오류가 발생한 경우 질문 수정 페이지로 돌아갑니다.
        }
        Question question = this.questionService.getQuestion(id); // ID에 해당하는 질문을 가져옵니다.
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다."); // 작성자가 아닌 경우 예외를 던집니다.
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent()); // 질문을 수정합니다.
        return String.format("redirect:/question/detail/%s", id); // 수정된 질문의 상세 페이지로 리다이렉트합니다.
    }

    // 인증된 사용자만 접근할 수 있도록 설정합니다.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id); // ID에 해당하는 질문을 가져옵니다.
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다."); // 작성자가 아닌 경우 예외를 던집니다.
        }
        this.questionService.delete(question); // 질문을 삭제합니다.
        return "redirect:/"; // 홈 페이지로 리다이렉트합니다.
    }
}