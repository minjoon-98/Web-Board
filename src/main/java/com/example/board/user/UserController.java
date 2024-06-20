package com.example.board.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Controller // 이 클래스가 Spring MVC 컨트롤러임을 나타냅니다.
@RequestMapping("/user") // "/user"로 시작하는 URL 요청을 처리할 수 있도록 매핑합니다.
public class UserController {

    // UserService 주입
    private final UserService userService;

    // GET 요청으로 "/signup" URL을 처리하여 회원가입 폼을 보여주는 메서드
    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form"; // 회원가입 폼을 반환
    }

    // POST 요청으로 "/signup" URL을 처리하여 회원가입을 처리하는 메서드
    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // 유효성 검사에서 오류가 발생한 경우
            return "signup_form"; // 회원가입 폼을 다시 반환
        }

        // 두 개의 패스워드가 일치하지 않는 경우
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form"; // 회원가입 폼을 다시 반환
        }

        try {
            // 유저를 생성하는 서비스 메서드 호출
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
        } catch (DataIntegrityViolationException e) { // 데이터 무결성 위반 예외 처리
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form"; // 회원가입 폼을 다시 반환
        } catch (Exception e) { // 기타 예외 처리
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form"; // 회원가입 폼을 다시 반환
        }

        return "redirect:/"; // 회원가입 성공 시 메인 페이지로 리다이렉트
    }

    // GET 요청으로 "/login" URL을 처리하여 로그인 폼을 보여주는 메서드
    @GetMapping("/login")
    public String login() {
        return "login_form"; // 로그인 폼을 반환
    }
}