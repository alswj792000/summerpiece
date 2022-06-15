package com.summerroot.summerpiece.controlller;

import com.summerroot.summerpiece.DTO.MemberDto;
import com.summerroot.summerpiece.DTO.ResponseDto;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
import com.summerroot.summerpiece.service.MailService;
import com.summerroot.summerpiece.service.MemberService;
import com.summerroot.summerpiece.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final EmailUtils emailUtils;

    @Autowired
    MemberSecuRepository memberSecuRepository; // 시큐리티 레파지토리
    private final MemberService memberService;
    private final MailService mailService;

    /** by민정 : 회원가입(등록) */
    @PostMapping("/member")
    public String signup(MemberDto memberDto) { // 회원 추가
        memberService.save(memberDto);
        return "redirect:/login"; // 회원가입 완료시 로그인 페이지로 이동
    }

    /** by민정 : 로그아웃*/
    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

    @GetMapping("/members/{memberId}/update")
    public String updateMemberForm(@PathVariable("memberId") Long memberId, Model model) {
        Member member = memberService.findOne(memberId);

        MemberDto dto = new MemberDto();
        dto.setEmail(member.getEmail());
        dto.setPwd(member.getPwd());
        dto.setName(member.getName());
        dto.setNickname(member.getNickname());
        dto.setPhone(member.getPhone());

        model.addAttribute("dto", dto);

        return "members/updateMemberForm";
    }

    @PostMapping("/members/{memberId}/update")
    public String updateMember(@PathVariable("memberId") Long memberId, @ModelAttribute("dto") MemberDto dto) {
        memberService.updateMember(memberId, dto.getName(), dto.getNickname(), dto.getPhone());

        return "redirect:/members/{memberId}/update";
    }

    @PostMapping("/members/{memberId}/updatePwd")
    public String updateMemberPwd(@PathVariable("memberId") Long memberId, @RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd) {
        // 원래 비밀번호가 일치하는지 비교

        memberService.updatePwd(memberId, newPwd);

        return "redirect:/members/{memberId}/update";
    }

    @GetMapping("/resetPwd")
    public String resetPwdForm() {
        return "members/resetPwdForm";
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public int sendCode(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String email = (String) param.get("email");
        String subject = "이메일 인증 코드입니다.";
        String code = createVerificationCode();
        String body = "이메일 인증 코드는 \"" + code + "\" 입니다.";

        int resultCode = emailUtils.sendEmail(email, subject, body);

        if (resultCode == 200) {
            HttpSession session = request.getSession();
            session.setAttribute(email, code);
            session.setMaxInactiveInterval(60);
        }

        return resultCode;
    }

    @PostMapping("/verifyCode")
    @ResponseBody
    public int checkCode(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String email = (String) params.get("email");
        String code = (String) params.get("code");
        String savedCode = (String) request.getSession().getAttribute(email);

        if (savedCode == null) {
            return 500;
        }

        if (savedCode.equals(code)) {
            return 200;
        } else {
            return 500;
        }
    }

    @PostMapping("/members/{memberId}/delete")
    @ResponseBody
    public int deleteMember(@PathVariable("memberId") Long memberId, @RequestBody Map<String, Object> param) {
        String rawPwd = (String) param.get("pwd");

        return memberService.deleteMember(memberId, rawPwd);
    }

    private String createVerificationCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int length = 6;

        return new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

        /** 회원가입 : 이메일 인증코드 발급 */
    @ResponseBody // 값 변환을 위해 꼭 필요함
    @PostMapping("/emailCode") // 아이디 중복확인을 위한 값으로 따로 매핑
    public String signUpEmailCheck(@RequestParam("email") String email) {
//        logger.info("전달받은 email:"+email);

        Random random=new Random();  //난수 생성을 위한 랜덤 클래스
        String key="";  // 인증번호
        for(int i =0; i<3;i++) {
            int index=random.nextInt(25)+65; //A~Z까지 랜덤 알파벳 생성
            key+=(char)index;
        }
        int numIndex=random.nextInt(9999)+1000; //4자리 랜덤 정수를 생성
        key+=numIndex;

        String subject = "[Summerpiece] 인증번호 입력을 위한 메일 전송.";
        String body = "이메일 인증 코드는 " + key + "입니다.";

        Map<String, Object> result = mailService.signUpEmailSend(email, subject, body);

        return key;
    }

    /** 회원가입 : 이메일 중복 확인 */
    @GetMapping("/emailCheck") // 데이터를 주는 컨트롤러이다.
    public @ResponseBody ResponseDto<String> emailCompareCheck(String email){
//        logger.info("전달받은 email:"+email);
        //1. SELECT * FROM member WHERE email = "ssar"; 유저 eintity 받음
        Member member =  memberSecuRepository.mEmailCompareCheck(email);

        //2. 있으면 json 응답 클래스 리턴
        if(member == null){
            return new ResponseDto<String>(1, "통신 성공", "중복안됨");
        } else {
            return new ResponseDto<String>(1, "통신 성공", "중복됨");
        }
    }
}
