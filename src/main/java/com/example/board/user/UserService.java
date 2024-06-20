package com.example.board.user;

import com.example.board.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Service // 이 클래스가 서비스 계층의 빈임을 나타냅니다.
public class UserService {

    private final UserRepository userRepository; // UserRepository 주입
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    // 새로운 사용자를 생성하는 메서드
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser(); // 새로운 사용자 객체 생성
        user.setUsername(username); // 사용자 이름 설정
        user.setEmail(email); // 사용자 이메일 설정
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호를 암호화하여 설정
        this.userRepository.save(user); // 사용자 객체를 데이터베이스에 저장
        return user; // 생성된 사용자 객체 반환
    }

    // 사용자 이름으로 사용자를 조회하는 메서드
    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username); // 사용자 이름으로 사용자 조회
        if (siteUser.isPresent()) { // 사용자가 존재하면
            return siteUser.get(); // 사용자 객체 반환
        } else {
            throw new DataNotFoundException("siteuser not found"); // 사용자가 없으면 예외를 던짐
        }
    }
}