package com.summerroot.summerpiece.repository;

import com.summerroot.summerpiece.domain.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CalendarRepository {

    private final EntityManager em;

    public List<Calendar> findCalendarList(Long memberId) {
        String jpql = "select c from Calendar c where c.calendarWriter.id = " + memberId;

        return em.createQuery(jpql, Calendar.class).getResultList();
    }

    public void save(Calendar calendar) {
        if(calendar.getId() == null){
            em.persist(calendar);
        } else {
            em.merge(calendar);
        }
    }

    public Calendar findOne(Long calendarId) {
        return em.find(Calendar.class, calendarId);
    }
}
