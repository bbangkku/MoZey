package com.ssafy.tenten.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다."),

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),

    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 질문을 찾을 수 없습니다." ),

    QUESTION_NOT_ENOUGH (HttpStatus.NOT_FOUND, "해당하는 정보의 질문 개수가 8개 이상이 안됩니다." ),

    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 뉴스를 찾을 수 없습니다." ),

    USER_NOT_ENOUGH(HttpStatus.NOT_FOUND,"친구가 부족합니다"),

    COININFO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 코인 정보를 찾을 수 없습니다"),

    MONEYHISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저의 코인 히스토리 정보가 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}