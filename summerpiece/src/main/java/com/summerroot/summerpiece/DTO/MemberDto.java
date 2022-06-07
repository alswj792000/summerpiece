package com.summerroot.summerpiece.DTO;

import com.summerroot.summerpiece.domain.MemberStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/** by 민정 : 폼으로 받을 회원정보를 매핑 시켜줄 객체를 만드는 작업
* */
@Getter
@Setter
public class MemberDto {
    // 이메일, 비밀번호, 권한번호 + a
    private String email;
    private String pwd;
    private String auth;
    // 이메일, 비밀번호, 권한번호를 제외한 경우 에러 발생
    private String name;
    private String nickname;
    private String phone;
    private LocalDateTime enrollDate;
    private MemberStatus status;
}
