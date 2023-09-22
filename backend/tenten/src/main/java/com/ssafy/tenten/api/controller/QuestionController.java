package com.ssafy.tenten.api.controller;

import com.ssafy.tenten.api.repository.QuestionRepository;
import com.ssafy.tenten.api.repository.VoteCntRepository;
import com.ssafy.tenten.api.repository.VoteHistoryRepository;
import com.ssafy.tenten.api.service.QuestionService;
import com.ssafy.tenten.api.service.VoteService;
import com.ssafy.tenten.dto.QuestionDto;
import com.ssafy.tenten.exception.ErrorResponseEntity;
import com.ssafy.tenten.exception.SuccessResponseEntity;
import com.ssafy.tenten.vo.Request.QuestionRequest;
import com.ssafy.tenten.vo.Response.QuestionResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ssafy.tenten.exception.ErrorCode.QUESTION_NOT_FOUND;

@RestController
@Api(tags = "Question")
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j 
public class QuestionController {
    private final VoteService voteService;
    private final VoteHistoryRepository voteHistoryRepository;
    private final VoteCntRepository voteCntRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ModelMapper mapper;

    /**
     * 질문 신청 내역 조회 - 완료 pageable 적용 ..
     * 사용자
     */

    @ApiOperation(value = "개인 질문 신청 내역 조회")
    @GetMapping(value = {"/questions/users/{userId}/{status}/","/questions/users/{userId}/"})
    public ResponseEntity<SuccessResponseEntity> getAppQuestion(@PathVariable("userId") Long id,
                                                                @PathVariable(name = "status",required = false) Character status,
                                                                @RequestParam(defaultValue = "0") int pageIdx){

        PageRequest pageRequest = PageRequest.of(pageIdx, 20 ,Sort.by("qtnId"));

        return SuccessResponseEntity.toResponseEntity("질문 신청 내역 조회 - 사용자",questionService.getQuestions(id,status,pageRequest));
    }
    /**
     * 질문 신청 등록 - 완료
     */
    @ApiOperation(value = "질문 신청 등록")
    @PostMapping("/questions")
    public ResponseEntity<?> postQuestions(@RequestBody QuestionRequest questionRequest){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        QuestionDto questionDto = mapper.map(questionRequest, QuestionDto.class);
        questionService.postQuestions(questionDto);

        return SuccessResponseEntity.toResponseEntity("질문 신청 등록 완료",null);
    }

    /**
     * 질문 신청 삭제 - 완료
     * 관리자 진짜 삭제
     */
    @DeleteMapping("/questions/{qtnId}/")
    public ResponseEntity<?> deleteQuestion(@PathVariable("qtnId") Long qtnId){
        try{
            questionRepository.deleteById(qtnId);
        }catch (Exception e){
            return ErrorResponseEntity.toResponseEntity(QUESTION_NOT_FOUND);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 등록 질문 전체 조회 - 완료
     * 관리자
     */
    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(){

        List<QuestionResponse> questions = questionService.getAllQuestions();

        return SuccessResponseEntity.toResponseEntity("등록된 질문 전체 조회 성공",questions);
    }

    /**
     * 등록 질문 단일 조회 - 완료
     * 상세 조회
     */
    @GetMapping("/questions/{qtnId}/")
    public ResponseEntity<?> getQuestion(@PathVariable("qtnId") Long qtnId){
        QuestionResponse question = questionService.getQuestion(qtnId);
        return SuccessResponseEntity.toResponseEntity("등록된 질문 상세 조회 성공",question);
    }

    /**
     * 등록 질문 수정 - 완료
     * 관리자
     * 1. p->N 삭제, 2. P->Y 등록, 3. P-> 전체 수정
     */
    @PutMapping("/questions/{qtnId}/")
    public ResponseEntity<?> updateQuestion(@PathVariable("qtnId") Long qtnId,@RequestBody QuestionRequest questionRequest){
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        QuestionDto questionDto = mapper.map(questionRequest, QuestionDto.class);
        QuestionResponse questionResponse = questionService.updateQuestion(qtnId, questionDto);
        return SuccessResponseEntity.toResponseEntity("등록 수정 완료",questionResponse);
    }


}
