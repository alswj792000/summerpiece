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

    @GetMapping("/calendar/schedule/one")
    public String addScheduleView(){
        return "calendar/calendarInsert";
    }

    @GetMapping("/calendar/schedule/one/{date}")
    public String addScheduleViewWithDate(Model model, @PathVariable String date){
        model.addAttribute("date", date);
        model.addAttribute("allDay", true);

        return "calendar/calendarInsert";
    }

    public Calendar addNew(Member member, String calendarContent, String calendarStart, String calendarEnd, boolean isAllDay, String calendarColor){
        Calendar calendar = new Calendar();

        String pattern = makePattern(isAllDay);

        LocalDateTime calendarStartDate = changeTimeFormat(calendarStart, pattern, isAllDay);
        LocalDateTime calendarEndDate = changeTimeFormat(calendarEnd, pattern, isAllDay);

        calendar.calendarInfoInit(member, calendarContent, calendarStartDate, calendarEndDate, isAllDay, calendarColor);

        calendarService.saveCalendar(calendar);

        return calendar;
    }

    public LocalDateTime changeTimeFormat(String date, String pattern, boolean isAllDay){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        if(isAllDay){
            return LocalDate.parse(date, df).atStartOfDay();
        } else {
            return LocalDateTime.parse(date, df);
        }
    }

    public String makePattern(boolean isAllDay){
        if(isAllDay){
            return "yyyy-MM-dd";
        } else {
            return "yyyy-MM-dd HH:mm:ss";
        }
    }

    @PostMapping("/calendar/schedule")
    @ResponseBody
    public String addSchedule(@AuthenticationPrincipal Member member, @RequestParam String calendarContent,
                              @RequestParam String calendarStart, @RequestParam String calendarEnd, @RequestParam(required = false) Boolean isAllDay,
                              @RequestParam String calendarColor){
        if(isAllDay == null){
            isAllDay = false;
        }

        Calendar calendar = addNew(member, calendarContent, calendarStart, calendarEnd, isAllDay, calendarColor);
        return calendar.getId().toString();
    }

    @PostMapping("/calendar/schedule/one")
    public String addOneSchedule(@AuthenticationPrincipal Member member, @RequestParam String calendarContent,
                                 @RequestParam String calendarStart, @RequestParam String calendarEnd, @RequestParam(required = false) Boolean isAllDay,
                                 @RequestParam String calendarColor){
        if(isAllDay == null){
            isAllDay = false;
        }

        System.out.println(calendarStart);
        addNew(member, calendarContent, calendarStart, calendarEnd, isAllDay, calendarColor);
        return "redirect:/calendar/view";
    }

    @GetMapping("/calendar/schedule/{id}")
    public String detailSchedule(@PathVariable Long id, Model model){
        Calendar calendar = calendarService.findCalendar(id);
        String startDate;
        String endDate;

        String startTime = null;
        String endTime = null;


        if(calendar.isAllDay()){
            startDate = calendar.getCalendarStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = calendar.getCalendarEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            String[] s = calendar.getCalendarStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).split("\\s");
            String[] e = calendar.getCalendarEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).split("\\s");

            startDate = s[0];
            startTime = s[1];

            endDate = e[0];
            endTime = e[1];
        }

        model.addAttribute("c", calendar);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "calendar/calendarDetail";
    }

    @PutMapping("/calendar/schedule/{id}")
    public String updateSchedule(@PathVariable("id") Long id, @RequestParam String calendarContent,
                                 @RequestParam String calendarStart, @RequestParam String calendarEnd, @RequestParam(required = false) Boolean isAllDay,
                                 @RequestParam String calendarColor, @ModelAttribute Calendar c){
        Calendar calendar = calendarService.findCalendar(id);

        String pattern = makePattern(isAllDay);

        LocalDateTime calendarStartDate = changeTimeFormat(calendarStart, pattern, isAllDay);
        LocalDateTime calendarEndDate = changeTimeFormat(calendarEnd, pattern, isAllDay);

        calendar.updateCalendar(calendarContent, calendarStartDate, calendarEndDate, isAllDay, calendarColor);
        calendarService.saveCalendar(calendar);

        return "redirect:/calendar/schedule/" + id;
    }

    @DeleteMapping("/calendar/schedule/{id}")
    public String deleteSchedule(@PathVariable("id") Long id){
        Calendar calendar = calendarService.findCalendar(id);
        calendar.deleteCalendar();

        calendarService.saveCalendar(calendar);
        return "redirect:/calendar/view";
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException() {
        return "error/500";
    }

}
