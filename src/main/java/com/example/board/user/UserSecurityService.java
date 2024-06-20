package com.example.board.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Service // 이 클래스가 서비스 계층의 빈임을 나타냅니다.
public class UserSecurityService implements UserDetailsService { // Spring Security에서 사용자 인증을 처리하기 위해 UserDetailsService 인터페이스를 구현합니다.

    private final UserRepository userRepository; // UserRepository 주입

    // 사용자 이름으로 사용자를 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username); // 사용자 이름으로 사용자 조회
        if (_siteUser.isEmpty()) { // 사용자가 없으면 예외를 던짐
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        SiteUser siteUser = _siteUser.get(); // Optional에서 사용자 객체를 가져옴
        List<GrantedAuthority> authorities = new ArrayList<>(); // 사용자 권한 목록을 저장할 리스트
        if ("admin".equals(username)) { // 사용자가 admin이면
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue())); // ADMIN 권한 추가
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue())); // USER 권한 추가
        }
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities); // UserDetails 객체 반환
    }
}