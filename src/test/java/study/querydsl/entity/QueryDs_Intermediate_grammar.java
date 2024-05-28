package study.querydsl.entity;


import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    void tupleProjectionTest() {

        // given...
        // @BeforeEach

        // when...
        List<Tuple> fetch = query.select(member.name, member.age).from(member).fetch();

        // then...
        // 1. tuple 은 queryDsl 에 종속적인 라이브러리이다.
        // 2. queryRepository 에서만 사용되어져야 한다.
        // 3. dto 로 변환해서 service 계층에 넘긴다.
        List<Member> collect = fetch.stream().map((tuple) -> {
            String name = tuple.get(member.name);
            Integer i = tuple.get(member.age);
            return new Member(name, i);
        }).collect(Collectors.toList());

        collect.forEach(member -> {
            log.info("member Name : {}", member.getName());
            log.info("member age : {}", member.getAge());
        });

    }


}
