package com.example.board.Question;

import com.example.board.DataNotFoundException;
import com.example.board.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Service // 이 클래스가 서비스 계층의 빈임을 나타냅니다.
public class QuestionService {

    // QuestionRepository 주입
    private final QuestionRepository questionRepository;

    // 페이지 번호에 따른 질문 목록을 가져오는 메서드
    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate")); // 질문을 생성 날짜 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지 번호와 페이지 크기, 정렬 기준 설정
        return this.questionRepository.findAll(pageable); // 페이징 처리된 질문 목록을 반환
    }

    // ID로 질문을 가져오는 메서드
    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id); // ID로 질문을 찾음
        if (question.isPresent()) {
            return question.get(); // 질문이 존재하면 반환
        } else {
            throw new DataNotFoundException("question not found"); // 질문이 존재하지 않으면 예외를 던짐
        }
    }

    // 새로운 질문을 생성하는 메서드
    public void create(String subject, String content, SiteUser user) {
        Question q = new Question(); // 새로운 질문 객체 생성
        q.setSubject(subject); // 질문 제목 설정
        q.setContent(content); // 질문 내용 설정
        q.setCreateDate(LocalDateTime.now()); // 현재 시간으로 생성 날짜 설정
        q.setAuthor(user); // 질문 작성자 설정
        this.questionRepository.save(q); // 질문을 데이터베이스에 저장
    }

    // 질문을 수정하는 메서드
    public void modify(Question question, String subject, String content) {
        question.setSubject(subject); // 새로운 질문 제목 설정
        question.setContent(content); // 새로운 질문 내용 설정
        question.setModifyDate(LocalDateTime.now()); // 현재 시간으로 수정 날짜 설정
        this.questionRepository.save(question); // 수정된 질문을 데이터베이스에 저장
    }

    // 질문을 삭제하는 메서드
    public void delete(Question question) {
        this.questionRepository.delete(question); // 데이터베이스에서 질문을 삭제
    }
}