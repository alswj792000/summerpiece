package com.summerroot.summerpiece.controlller;

import com.summerroot.summerpiece.domain.Calendar;
import com.summerroot.summerpiece.domain.Member;
import com.summerroot.summerpiece.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/calendar/view")
    public String calendarView(){
        return "calendar/calendarMain";
    }

    @GetMapping("/calendar/schedule-list")
    @ResponseBody
    public List<Calendar> getScheduleList(@AuthenticationPrincipal Member member){
        List<Calendar> calendarList = calendarService.findCalendarList(member.getId());
        return calendarList;
    }

    @GetMapping("/calendar/schedule")
    public String addScheduleView(){
        return "calendar/calendarInsert";
    }

    @GetMapping("/calendar/schedule/{date}")
    public String addScheduleView2(Model model, @PathVariable String date){
        model.addAttribute("date", date);
        model.addAttribute("allDay", true);

        return "calendar/calendarInsert";
    }

    @PostMapping("/calendar/schedule")
    @ResponseBody
    public String addSchedule(@AuthenticationPrincipal Member member, @RequestParam String calendarContent,
                              @RequestParam String calendarStart, @RequestParam String calendarEnd, @RequestParam boolean isAllDay, @RequestParam String calendarColor){
        Calendar calendar = new Calendar();

        LocalDateTime calendarStartDate;
        LocalDateTime calendarEndDate;
        DateTimeFormatter df;

        if(isAllDay){
            df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            calendarStartDate = LocalDate.parse(calendarStart, df).atStartOfDay();
            calendarEndDate = LocalDate.parse(calendarEnd, df).atStartOfDay();
        } else {
            df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            calendarStartDate = LocalDateTime.parse(calendarStart, df);
            calendarEndDate = LocalDateTime.parse(calendarEnd, df);
        }

        System.out.println(calendarStartDate);
        System.out.println(calendarEndDate);

        calendar.calendarInfoInit(member, calendarContent, calendarStartDate, calendarEndDate, isAllDay, calendarColor);

        calendarService.saveCalendar(calendar);
        return calendar.getId().toString();
    }

    @GetMapping("/calendar/schedule/{id}")
    public String detailSchedule(@PathVariable Long id){
        System.out.println(id);
        Calendar calendar = calendarService.findCalendar(id);
        return "calendar/calendarDetail";
    }

    @PutMapping("/calendar/schedule/{id}")
    @ResponseBody
    public String updateSchedule(@PathVariable("id") Long id, @ModelAttribute Calendar calendar){
        calendar.updateCalendar(id);
        calendarService.updateCalendar(calendar);

        return id.toString();
    }

    @DeleteMapping("/calendar/schedule/{id}")
    @ResponseBody
    public String deleteSchedule(@PathVariable("id") Long id){
        calendarService.deleteCalendar(id);
        return id.toString();
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException() {
        return "error/500";
    }

}
