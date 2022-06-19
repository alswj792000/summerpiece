package com.summerroot.summerpiece.repository;

import com.summerroot.summerpiece.domain.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CalendarRepository {

    private final EntityManager em;

    public List<Calendar> findCalendarList(Long memberId) {
        String jpql = "select c from Calendar c where c.calendarWriter.id = " + memberId + "and c.calendarState = 'Y'";

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

    public Long findCalendarCount(Long id) {
        LocalDateTime now = LocalDateTime.now();

        String jpql = "select count(c) from Calendar c where c.calendarWriter.id = " + id +
                " and c.calendarEndDate > :now ";

        return (Long)em.createQuery(jpql)
                .setParameter("now", now)
                .getSingleResult();

    }
}
