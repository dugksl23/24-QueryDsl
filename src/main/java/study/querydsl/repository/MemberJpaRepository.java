package study.querydsl.repository;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;

import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;
import static study.querydsl.entity.QTeamMember.teamMember;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;
    private final MemberRepository memberRepository;

    public Member findById(Long id) {
        String jpql = "SELECT m FROM Member m WHERE m.id = :id";
        return em.createQuery(jpql, Member.class).setParameter("id", id).getSingleResult();
    }

    public List<Member> findAll(Long id) {
        String jpql = "SELECT m FROM Member m";
        return em.createQuery(jpql, Member.class).getResultList();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition searchCondition) {
        return query.select(new QMemberTeamDto(member.id.as("memberId"), member.name.as("memberName"), member.age, team.id.as("teamId"), team.name.as("teamName"))).from(member).leftJoin(member.teamMembers, teamMember).leftJoin(teamMember.team, team).where(memberNameEq(searchCondition.getMemberName()), teamNameEq(searchCondition.getTeamName()), memberAgeGoe(searchCondition.getAgeGoe()), memberAgeLoe(searchCondition.getAgeLoe())).fetch();
    }


    private BooleanExpression memberNameEq(String memberName) {

        if (StringUtils.isBlank(memberName)) {
            return null;
        }

        return member.name.eq(memberName);
    }

    private BooleanExpression teamNameEq(String teamName) {

        if (StringUtils.isBlank(teamName)) {
            return null;
        }

        return team.name.eq(teamName);
    }

    private BooleanExpression memberAgeGoe(Integer memberAge) {

        if (memberAge == null) {
            return null;
        }

        return member.age.goe(memberAge);
    }

    private BooleanExpression memberAgeLoe(Integer memberAge) {

        if (memberAge == null) {
            return null;
        }

        return member.age.loe(memberAge);
    }



}
