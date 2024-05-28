package study.querydsl.entity;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Slf4j
@Transactional
public class QueryDs_Intermediate_grammar {

    @Autowired
    private EntityManager em;
    private JPAQueryFactory query;
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void init() {
        query = new JPAQueryFactory(em);
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
        List<MemberDto> fetch = query
                .select(Projections.bean(MemberDto.class,
                        member.age,
                        member.name))
                .from(member)
                .fetch();

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
        List<MemberDto> fetch = query
                .select(Projections.fields(MemberDto.class,
                        member.name,
                        member.age))
                .from(member)
                .fetch();

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
        List<MemberDto> fetch = query
                .select(Projections.constructor(MemberDto.class,
                        member.name.as("name"),
                        member.age))
                .from(member)
                .fetch();

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
        List<MemberDto> fetch = query.select(Projections.constructor(MemberDto.class,
                        member.name.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(subMember.age.max())
                                .from(subMember), "age")))
                .from(member)
                .fetch();

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
        List<MemberDto> fetch = query
                .select(new QMemberDto(member.name, member.age))
                .from(member)
                .fetch();

        // then...
        fetch.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member Age : {}", member.getAge());
        });
    }

}
