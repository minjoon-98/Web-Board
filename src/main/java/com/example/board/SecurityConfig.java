package com.example.board;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 이 클래스가 설정 클래스임을 나타냅니다.
@EnableWebSecurity // Spring Security를 활성화합니다.
@EnableMethodSecurity(prePostEnabled = true) // 메서드 보안 활성화 (예: @PreAuthorize 어노테이션 사용 가능).
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 요청에 대한 보안 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        // 모든 요청을 허용 (실제로는 보안 위험이 있습니다).
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                        /** 위와 같이 모든 경로에 대해 접근을 허용하게 된다면 아래와 같은 문제가 생긴다.
                         * 인증 및 인가의 부재:
                         * 애플리케이션의 모든 경로에 접근이 허용되므로, 인증되지 않은 사용자도 모든 페이지와 API에 접근할 수 있습니다. 이는 민감한 정보나 관리자 페이지 등의 보호된 리소스에 대한 접근을 막지 못하게 됩니다.
                         *
                         * CSRF 공격에 취약:
                         * 모든 경로가 공개되면 CSRF (Cross-Site Request Forgery) 공격에 취약해질 수 있습니다. CSRF는 공격자가 피해자의 권한을 이용해 원하지 않는 작업을 수행하게 만드는 공격입니다.
                         *
                         * 의도하지 않은 접근 허용:
                         * 애플리케이션의 특정 부분(예: 관리자 페이지, 사용자 정보 등)에 대한 접근이 의도치 않게 허용될 수 있습니다. 이는 데이터 유출 및 권한 없는 작업 수행으로 이어질 수 있습니다.
                         * */
//                        .requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll() // 공용 경로
//                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN") // 관리자 경로
//                        .requestMatchers(new AntPathRequestMatcher("/user/**")).authenticated() // 사용자 경로
                        /* 아래와 같은 방법이 있다 thanks to SamChoi */
//                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/user/login")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/**.css")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/**.html")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/**.js")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/question/list")).permitAll())
                // CSRF 보호 설정
                .csrf((csrf) -> csrf
                        // 특정 경로(h2-console)에 대해 CSRF 보호를 비활성화
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                // 헤더 설정
                .headers((headers) -> headers
                        // X-Frame-Options 헤더를 SAMEORIGIN으로 설정 (iframe 사용을 제한)
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                // 폼 로그인 설정
                .formLogin((formLogin) -> formLogin
                        // 사용자 정의 로그인 페이지 경로 설정
                        .loginPage("/user/login")
                        // 로그인 성공 시 기본 경로 설정
                        .defaultSuccessUrl("/"))
                // 로그아웃 설정
                .logout((logout) -> logout
                        // 사용자 정의 로그아웃 경로 설정
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        // 로그아웃 성공 시 리다이렉트 경로 설정
                        .logoutSuccessUrl("/")
                        // 세션 무효화
                        .invalidateHttpSession(true))
        ;
        return http.build(); // SecurityFilterChain 객체를 생성하고 반환
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder를 사용하여 비밀번호를 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // AuthenticationManager 빈을 생성하고 반환
        return authenticationConfiguration.getAuthenticationManager();
    }
}