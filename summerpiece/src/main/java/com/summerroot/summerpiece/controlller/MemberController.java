package com.summerroot.summerpiece.controlller;

import com.summerroot.summerpiece.DTO.MemberDto;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
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

    @PostMapping("/resetPwd")
    @ResponseBody
    public int resetPwd(@RequestBody Map<String, Object> params) {
        String email = (String) params.get("email");
        String newPwd = (String) params.get("newPwd");

        memberService.resetPwd(email, newPwd);

        return 200;
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
}
