package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;

import static study.querydsl.entity.QMember.member;

@Repository
public class MemberQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    @Autowired
    public MemberQueryRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public Member findById(Long id) {

        QMember m = member;
        QMember m1 = new QMember("m");
        return query.select(m)
                .from(m)
                .where(m.id.eq(id))
                .fetchOne();

    }



}
