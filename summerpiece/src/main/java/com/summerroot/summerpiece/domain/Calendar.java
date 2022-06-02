package com.summerroot.summerpiece.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.summerroot.summerpiece.converter.BooleanToYNConverter;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter
public class Calendar {

    @Id @GeneratedValue
    @Column(name = "calendar_id")
    private Long id;

    private LocalDateTime calendarStartDate;
    private LocalDateTime calendarEndDate;
    private LocalDateTime calendarModifyDate;

    @Convert(converter = BooleanToYNConverter.class)
    private boolean isAllDay;

    @Enumerated(EnumType.STRING)
    private CalendarState calendarState;
    private String calendarContent;
    private String calendarColor;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "member_id")
    private Member calendarWriter;

    public void calendarInfoInit(Member calendarWriter, String calendarContent, LocalDateTime calendarStartDate, LocalDateTime calendarEndDate, boolean isAllDay, String calendarColor){
        updateCalendar(calendarContent, calendarStartDate, calendarEndDate, isAllDay, calendarColor);
        this.calendarWriter = calendarWriter;
    }

    public void deleteCalendar(){
        this.calendarState = CalendarState.N;
    }

    public void updateCalendar(String calendarContent, LocalDateTime calendarStartDate, LocalDateTime calendarEndDate, boolean isAllDay, String calendarColor){
        this.calendarContent = calendarContent;
        this.calendarStartDate = calendarStartDate;
        this.calendarEndDate = calendarEndDate;
        this.isAllDay = isAllDay;
        this.calendarModifyDate = LocalDateTime.now();
        this.calendarState = CalendarState.Y;
        this.calendarColor = calendarColor;
    }
}