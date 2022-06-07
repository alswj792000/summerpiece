package com.summerroot.summerpiece.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;
    private String pwd;
    private String name;
    private String nickname;
    private String phone;
    private LocalDateTime enrollDate;

    /** by민정. 20220519 추가 */
//    @Column(name = "auth")
    private String auth; // admin, user

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @OneToMany(mappedBy = "fileUploadMember", cascade = CascadeType.ALL)
    private List<FileBox> fileBoxes = new ArrayList<>();

    @OneToMany(mappedBy = "boardWriter", cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "replyWriter", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "calendarWriter", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Calendar> calendars = new ArrayList<>();

    /** by민정 : 스프링부트 */
    @Builder
    public Member(String email, String pwd, String name, String nickname, String phone, String auth,
                  LocalDateTime enrollDate, MemberStatus status) {
        this.email = email;
        this.pwd = pwd;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.auth = auth;
        // status, enrollDate추가
        this.status = MemberStatus.Y;
        this.enrollDate = LocalDateTime.now(); // 시간,날짜 동시에 필요할 때 사용
    }

    /** ------ UserDetails에서 필수로 구현해야 하는 메소드 ------*/
    // 사용자의 권한을 콜렉션 형태로 반환 (단, 클래스 자료형은 GrantedAuthority를 구현해야함)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (String role : auth.split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
    }

    // 사용자의 id(email)를 반환 (unique한 값)
    @Override
    public String getUsername() {
        return email;
    }

    // 사용자의 password를 반환
    @Override
    public String getPassword() {
        return pwd;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        if (status == MemberStatus.N) {
            return false;
        }

        return true; // 사용 가능
    }

    public void updateMember(String name, String nickname, String phone) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
    }

    public void updatePwd(String newPwd) {
        this.pwd = newPwd;
    }

    public void deleteMember() {
        this.status = MemberStatus.N;
    }
}