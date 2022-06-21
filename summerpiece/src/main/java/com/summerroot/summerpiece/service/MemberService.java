package com.summerroot.summerpiece.service;

import com.summerroot.summerpiece.DTO.MemberDto;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.exception.ServiceException;
import com.summerroot.summerpiece.repository.MemberRepository;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberSecuRepository memberSecuRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /** by민정 : 회원정보 저장 */
    public Long save(MemberDto memberDto) {
        memberDto.setPwd(passwordEncoder.encode(memberDto.getPwd()));

        return memberSecuRepository.save(Member.builder()
                .email(memberDto.getEmail())
                .auth(memberDto.getAuth())
                .name(memberDto.getName())
                .nickname(memberDto.getNickname())
                .phone(memberDto.getPhone())
                .status(memberDto.getStatus())
                .enrollDate(memberDto.getEnrollDate())
                .pwd(memberDto.getPwd()).build()).getId();
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public void updateMember(Long memberId, String name, String nickname, String phone) {
        Member member = memberRepository.findOne(memberId);

        member.updateMember(name, nickname, phone);
    }

    public void updatePwd(Long memberId, String newPwd) {
        Member member = memberRepository.findOne(memberId);

        member.updatePwd(passwordEncoder.encode(newPwd));
    }

    public void deleteMember(Long memberId, String rawPwd) throws ServiceException {
        Member member = memberRepository.findOne(memberId);
        String encodedPwd = member.getPwd();

        if (passwordEncoder.matches(rawPwd, encodedPwd)) {
            member.deleteMember();
        } else {
            throw new ServiceException("회원 탈퇴에 실패했습니다.");
        }
    }

    public void resetPwd(String email, String pwd) {
        Member member = memberSecuRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException((email)));

        member.setPwd(passwordEncoder.encode(pwd));
    }

    public String findEmail(String name, String phone) throws ServiceException {
        Member member = memberRepository.findEmail(name, phone).orElseThrow(() -> new ServiceException("입력하신 정보에 해당하는 계정이 존재하지 않습니다."));

        return member.getEmail();
    }

    public boolean checkPwd(Long memberId, String oldPwd) {
        Member member = memberRepository.findOne(memberId);
        String encodedPwd = member.getPwd();

        if (!passwordEncoder.matches(oldPwd, encodedPwd)) {
            return false;
        }

        return true;
    }
}
