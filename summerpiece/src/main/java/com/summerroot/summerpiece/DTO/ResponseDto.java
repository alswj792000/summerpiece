package com.summerroot.summerpiece.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 회원가입 : 이메일 중복 확인 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    private Integer code; // -1 실패, 1 성공
    private String msg; // 왜 통신을 실패했는 지에 대한 내용
    private T data; // 뭘 담을지 모르겠으니 응답할 때 정하겠다.
    // body data 담기
}

