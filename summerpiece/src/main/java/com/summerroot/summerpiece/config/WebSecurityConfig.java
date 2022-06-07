package com.summerroot.summerpiece.config;

import com.summerroot.summerpiece.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** by민정 : Spring Security */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService; // 유저정보를 가져올 클래스

    @Override
    public void configure(WebSecurity web) { // 인증을 무시할 경로들을 설정
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**","/h2-console/**"
                ,"/favicon.ico", "/resources/**", "/error"
                ,"/lib/**");
        /* (20220514) css, js, ,img일 경우 무조건 접근 가능(인증 무시)
        *  (20220517) h2콘솔 추가
        *  (20220519) 스프링시큐리티 로그인 성공시 발생하는 에러 해결위해 파비콘 추가
        *  (20220520) lib 추가(추가하지 않았을 경우 로그인전에 lib가 적용되지 않고, 로그인 후 lib경로가 url에 입력되는 오류 발생)
        * */
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { // http 관련 인증 설정
        http.csrf().disable(); // csrf 방지 (보안에 취약)
        http
                .authorizeRequests() // 접근에 대한 인증 설정
                .antMatchers("/login", "/signup", "/member", "/resetPwd", "/sendCode", "/verifyCode").permitAll() // 누구나 접근 허용
                .antMatchers("/").hasRole("USER") // USER, ADMIN만 접근 가능
                .antMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 가능
                .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
                .and()
                .formLogin() // 로그인에 관한 설정
                .loginPage("/login") // 로그인 페이지 링크
                .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 주소 (메인페이지로 설정)
                .and()
                .logout() // 8 : 로그아웃에 관한 설정
                .logoutSuccessUrl("/login") // 로그아웃 성공시 리다이렉트 주소(로그인 페이지로 설정)
                .invalidateHttpSession(true); // 로그아웃 이후 세션 전체 삭제 여부
    }

    /** by.민정 : 비밀번호 암호화 */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception { // 로그인할 때 필요한 정보
        auth.userDetailsService(memberService) // 유저 정보를 가져오는 서비스를 memberService으로 지정
                .passwordEncoder(new BCryptPasswordEncoder());
                // 패스워드 인코더는 빈으로 등록해놓은 passwordEncoder()를 사용 (BCrypt)
    }
}
