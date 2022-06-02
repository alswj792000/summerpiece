package com.summerroot.summerpiece.service;

import com.summerroot.summerpiece.domain.Calendar;
import com.summerroot.summerpiece.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public List<Calendar> findCalendarList(Long id) {
        return calendarRepository.findCalendarList(id);
    }

    @Transactional
    public void saveCalendar(Calendar calendar) {
        calendarRepository.save(calendar);
    }

    public Calendar findCalendar(Long id) {
        return calendarRepository.findOne(id);
    }
}
