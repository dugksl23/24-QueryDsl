package study.querydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<MemberTeamDto> searchByMemberTeamDto(MemberSearchCondition searchCondition) {
        return query.select(new QMemberTeamDto(member.id.as("memberId"), member.name.as("memberName"), member.age, team.id.as("teamId"), team.name.as("teamName"))).from(member).leftJoin(member.teamMembers, teamMember).leftJoin(teamMember.team, team).where(memberNameEq(searchCondition.getMemberName()), teamNameEq(searchCondition.getTeamName()), memberAgeGoe(searchCondition.getAgeGoe()), memberAgeLoe(searchCondition.getAgeLoe())).fetch();
    }

    public List<Member> searchByMember(MemberSearchCondition searchCondition) {
        return query.select(member).from(member).leftJoin(member.teamMembers, teamMember).leftJoin(teamMember.team, team).where(memberNameEq(searchCondition.getMemberName()), teamNameEq(searchCondition.getTeamName()), memberAgeBetween(searchCondition.getAgeGoe(), searchCondition.getAgeLoe()), null).fetch();
    }

    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> results = query
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.name.as("memberName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .where(memberNameEq(condition.getMemberName()), teamNameEq(condition.getTeamName()),
                        memberAgeBetween(condition.getAgeGoe(), condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();// fetch Query 와 Count Query 두번 질의

        List<MemberTeamDto> content = results.getResults();
        long total = results.getTotal(); //전체 record 수

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {

        List<MemberTeamDto> fetch = query
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.name.as("memberName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .where(memberNameEq(condition.getMemberName()), teamNameEq(condition.getTeamName()),
                        memberAgeBetween(condition.getAgeGoe(), condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();// fetch Query 1번만 실행


        long count = query.select(member)
                .from(member)
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .where(memberNameEq(condition.getMemberName()), teamNameEq(condition.getTeamName()),
                        memberAgeBetween(condition.getAgeGoe(), condition.getAgeLoe()))
                .fetchCount();

        return new PageImpl<>(fetch, pageable, count);
    }

    public Page<MemberTeamDto> searchPagingCountQueryOptimization(MemberSearchCondition condition, Pageable pageable) {

        List<MemberTeamDto> fetch = query
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.name.as("memberName"),
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")))
                .from(member)
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .where(memberNameEq(condition.getMemberName()), teamNameEq(condition.getTeamName()),
                        memberAgeBetween(condition.getAgeGoe(), condition.getAgeLoe()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();// fetch Query 1번만 실행


        JPAQuery<Member> countQuery = query.select(member)
                .from(member)
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .where(memberNameEq(condition.getMemberName()), teamNameEq(condition.getTeamName()),
                        memberAgeBetween(condition.getAgeGoe(), condition.getAgeLoe()));


        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchCount);
    }


    private BooleanExpression memberAgeBetween(int ageGoe, int ageLoe) {

        return memberAgeGoe(ageGoe).and(memberAgeLoe(ageLoe));
        //        return member.age.between(ageGoe, ageLoe);
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

