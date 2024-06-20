package com.example.board.Answer;

import com.example.board.DataNotFoundException;
import com.example.board.Question.Question;
import com.example.board.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해주는 Lombok 어노테이션입니다.
@Service // 이 클래스가 서비스 계층의 빈임을 나타냅니다.
public class AnswerService {

    // AnswerRepository 주입
    private final AnswerRepository answerRepository;

    // 답변을 생성하는 메서드
    public void create(Question question, String content, SiteUser author) {
        Answer answer = new Answer(); // 새로운 답변 객체 생성
        answer.setContent(content); // 답변 내용 설정
        answer.setCreateDate(LocalDateTime.now()); // 현재 시간으로 생성 날짜 설정
        answer.setQuestion(question); // 답변이 속한 질문 설정
        answer.setAuthor(author); // 답변 작성자 설정
        this.answerRepository.save(answer); // 답변을 데이터베이스에 저장
    }

    // ID로 답변을 가져오는 메서드
    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id); // ID로 답변을 찾음
        if (answer.isPresent()) {
            return answer.get(); // 답변이 존재하면 반환
        } else {
            throw new DataNotFoundException("answer not found"); // 답변이 존재하지 않으면 예외를 던짐
        }
    }

    // 답변을 수정하는 메서드
    public void modify(Answer answer, String content) {
        answer.setContent(content); // 새로운 답변 내용 설정
        answer.setModifyDate(LocalDateTime.now()); // 현재 시간으로 수정 날짜 설정
        this.answerRepository.save(answer); // 수정된 답변을 데이터베이스에 저장
    }

    // 답변을 삭제하는 메서드
    public void delete(Answer answer) {
        this.answerRepository.delete(answer); // 데이터베이스에서 답변을 삭제
    }
}