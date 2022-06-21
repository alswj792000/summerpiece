package com.summerroot.summerpiece.service;

import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberSecuRepository memberSecuRepository;

    /** by민정 : 로그인 */
    @Override
    public Member loadUserByUsername(String email) throws UsernameNotFoundException { // 필수 메소드인 loadUserByUsername()를 구현
        // 기본 반환 타입인 UserDetails를 Member로 변경, Member은 UserDetails을 상속받았기 때문에 자동으로 다운 캐스팅
        return memberSecuRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException((email)));
    }
}
