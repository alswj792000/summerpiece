package com.summerroot.summerpiece.controlller;

import com.summerroot.summerpiece.DTO.MemberDto;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.repository.MemberSecuRepository;
import com.summerroot.summerpiece.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final EmailUtil emailUtil;

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

        // 새로운 비밀번호로 변경
        memberService.updatePwd(memberId, newPwd);

        return "redirect:/members/{memberId}/update";
    }

    @GetMapping("/checkEmail")
    public String checkEmailForm() {
        return "members/checkEmailForm";
    }

    @PostMapping("/checkEmail")
    public String checkEmail(@RequestParam("email") String email) {
        System.out.println("email " + email);
        String subject = "이메일 인증 코드입니다.";
        String body = "이메일 인증 코드는 " + "입니다.";

        Map<String, Object> result = emailUtil.sendEmail(email, subject, body);

        return "redirect:/checkCode";
    }

    @GetMapping("/checkCode")
    public String checkCodeForm() {
        return "members/checkCodeForm";
    }

    @PostMapping("/checkCode")
    public String checkCode() {
        return "redirect:/login";
    }
}
