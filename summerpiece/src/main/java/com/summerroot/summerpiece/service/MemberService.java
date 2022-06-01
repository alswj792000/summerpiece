package com.summerroot.summerpiece.service;

import com.summerroot.summerpiece.DTO.MemberDto;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.MemberRepository;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberSecuRepository memberSecuRepository;
    private final MemberRepository memberRepository;

    /** by민정 : 로그인 */
    @Override
    public Member loadUserByUsername(String email) throws UsernameNotFoundException { // 필수 메소드인 loadUserByUsername()를 구현
        // 기본 반환 타입인 UserDetails를 Member로 변경, Member은 UserDetails을 상속받았기 때문에 자동으로 다운 캐스팅
        return memberSecuRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException((email)));
    }

    /** by민정 : 회원정보 저장 */
    public Long save(MemberDto memberDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberDto.setPwd(encoder.encode(memberDto.getPwd()));

        return memberSecuRepository.save(Member.builder()
                .email(memberDto.getEmail())
                .auth(memberDto.getAuth())
                .name(memberDto.getName())
                .nickname(memberDto.getNickname())
                .phone(memberDto.getPhone())
                .pwd(memberDto.getPwd()).build()).getId();
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public void updateMember(Long memberId, String pwd, String name, String nickname, String phone) {
        Member member = memberRepository.findOne(memberId);

        member.updateMember(pwd, name, nickname, phone);
    }
}
