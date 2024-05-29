package study.querydsl.entity;


import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.MemberRepositoryCustom;

import java.util.List;
import java.util.stream.IntStream;

import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Slf4j
@Transactional
public class QueryDs_Intermediate_grammar {

    @Autowired
    private EntityManager em;
    @Autowired
    private JPAQueryFactory query;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @BeforeEach
    public void init() {
//        query = new JPAQueryFactory(em);
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Member member = new Member("member" + i, i);
            Team team = new Team("team" + i);
            member.addTeam(team);
            memberRepository.save(member);
        });
    }

    @Test
    void stringProjectionTest() {

        // given...
        // @BeforeEach

        // when...
        List<String> fetch = query.select(member.name).from(member).fetch();

        // then...
        fetch.forEach(member -> log.info("member Name : {}", member));

    }


    @Test
    void jpaDtoProjectionTest() {

        // given...
        // @BeforeEach

        // when...
        String jpql = "select new study.querydsl.dto.MemberDto(m.name, m.age) from Member m";
        List<MemberDto> fetch = em.createQuery(jpql, MemberDto.class).getResultList();
        ;

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member);
            log.info("member Age : {}", member.getAge());
        });
    }

    @Test
    void findDtoBySetter() {

        // given...
        // @BeforeEach

        // when...
        List<MemberDto> fetch = query.select(Projections.bean(MemberDto.class, member.age, member.name)).from(member).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member);
            log.info("member Age : {}", member.getAge());
        });
    }

    @Test
    void findDtoByFields() {

        // given...
        // @BeforeEach

        // when...
        List<MemberDto> fetch = query.select(Projections.fields(MemberDto.class, member.name, member.age)).from(member).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }

    @Test
    void findDtoByConstructor() {

        // given...
        // @BeforeEach

        // when...
        List<MemberDto> fetch = query.select(Projections.constructor(MemberDto.class, member.name.as("name"), member.age)).from(member).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }


    @Test
    void findDtoByFields_ReturnSubQuery() {

        // given...
        // @BeforeEach
        QMember subMember = new QMember("subMember");

        // when...
        List<MemberDto> fetch = query.select(Projections.constructor(MemberDto.class, member.name.as("name"), ExpressionUtils.as(JPAExpressions.select(subMember.age.max()).from(subMember), "age"))).from(member).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }

    @Test
    void findDtoByQueryProjection() {

        // given...
        // @BeforeEach

        // when...
        List<MemberDto> fetch = query.select(new QMemberDto(member.name, member.age)).from(member).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }

    @Test
    void dynamicQuery_whereParam() {

        // given...
        // @BeforeEach
        String memberName = "member1";
        Integer age = 1;

        // when...
        List<MemberDto> fetch = query.select(new QMemberDto(member.name, member.age)).from(member).where(userNameEq(memberName), userAgeEq(age)).fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }


    private BooleanExpression userNameEq(String name) {

        return StringUtils.isBlank(name) ? null : member.name.eq(name);
//        if (StringUtils.isBlank(name)) {
//            return null;
//        }
//        return member.name.eq(name);
    }

    private BooleanExpression userAgeEq(Integer age) {

        if (age == null) {
            return null;
        }
        return member.age.eq(age);
    }


    @Test
    public void bulkUpdate() {

        // given...
        QMember subQueryMember = new QMember("subQueryMember");
        // when ...
        query.update(member).set(member.age, member.age.multiply(2)).where(member.age.lt(4)).execute();

        em.flush();
        em.clear();

        // then...
        List<Member> fetch = query.select(member).from(member).fetch();
        fetch.forEach(member -> log.info("member age : {}", member.getAge()));
    }

    @Test
    public void bulkDelete() {

        // given...
        QMember subQueryMember = new QMember("subQueryMember");
        // when ...
        query.delete(member).where(member.age.lt(3)).execute();

        em.flush();
        em.clear();

        // then...
        List<Member> fetch = query.select(member).from(member).fetch();
        fetch.forEach(member -> log.info("member age : {}", member.getAge()));
    }

    @Test
    public void sqlFunction() {

        // when ...
        String replaceFunction = "function('replace', {0}, {1}, {2})";
        String s = query.select(Expressions.stringTemplate(replaceFunction, member.name, "member", "m")).from(member).fetchFirst();


        String lower = "function('lower', {0})";
        String s1 = query.select(member.name)
                .from(member)
                .where(member.name.eq
                        (Expressions.stringTemplate(lower, member.name))
                ).fetchFirst();

        String s2 = query.select(member.name)
                .from(member)
                .where(member.name.eq
                        (member.name.lower())
                ).fetchFirst();


        // then...
        log.info("member name : {}", s);
        log.info("member name : {}", s1);
        log.info("member name : {}", s2);

    }


    @Test
    public void searchMemberDtoBySearchCondTest() {

        // given ...
        MemberSearchCondition condition = new MemberSearchCondition();
        String memberName = "member1";
        String teamName = "team1";
        int ageGoe = 1;
        int ageLoe = 4;

//        condition.setMemberName(memberName);
//        condition.setTeamName(teamName);
        condition.setAgeGoe(ageGoe);
        condition.setAgeLoe(ageLoe);

        // when...
        List<MemberTeamDto> memberTeamDtos = memberJpaRepository.searchMemberDtoByBuilder(condition);


        // then...
        memberTeamDtos.forEach(member -> {
            log.info("member id : {}", member.getMemberId());
            log.info("member name : {}", member.getMemberName());
            log.info("team id : {}", member.getTeamId());
            log.info("team name : {}", member.getTeamName());
            log.info("age : {}", member.getAge());
        });

    }

    @Test
    public void searchMemberBySearchCondTest() {

        // given ...
        MemberSearchCondition condition = new MemberSearchCondition();
        String memberName = "member1";
        String teamName = "team1";
        int ageGoe = 1;
        int ageLoe = 4;

        condition.setMemberName(memberName);
        condition.setTeamName(teamName);
        condition.setAgeGoe(ageGoe);
        condition.setAgeLoe(ageLoe);

        // when...
        List<Member> memberTeamDtos = memberJpaRepository.searchMemberByBuilder(condition);


        // then...
        memberTeamDtos.forEach(member -> {
            log.info("age : {}", member.getAge());
        });

    }


    @Test
    public void searchByMemberRepositoryCustom() {

        // given ...
        MemberSearchCondition condition = new MemberSearchCondition();
        String memberName = "member1";
        String teamName = "team1";
        int ageGoe = 1;
        int ageLoe = 4;

        condition.setMemberName(memberName);
        condition.setTeamName(teamName);
        condition.setAgeGoe(ageGoe);
        condition.setAgeLoe(ageLoe);

        // when...
        List<Member> memberTeamDtos = memberRepository.searchByMember(condition);

        // then...
        memberTeamDtos.forEach(member -> {
            log.info("age : {}", member.getAge());
        });

    }


}