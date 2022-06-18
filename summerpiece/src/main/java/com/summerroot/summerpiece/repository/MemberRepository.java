package com.summerroot.summerpiece.repository;

import com.summerroot.summerpiece.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public Optional<Member> findEmail(String name, String phone) {
        List<Member> members = em.createQuery("select m from Member m where m.name = :name and m.phone = :phone", Member.class)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .getResultList();

        return members.stream().findAny();
    }
}
