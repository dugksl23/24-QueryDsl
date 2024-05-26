package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.repository.MemberQueryRepository;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamMemberRepository;
import study.querydsl.repository.TeamRepository;

import java.util.List;
import java.util.stream.IntStream;

import static study.querydsl.entity.QMember.member;


@SpringBootTest
@Slf4j
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberQueryRepository queryRepository;

    @Autowired
    private EntityManager em;

    private JPAQueryFactory query;


    @BeforeEach
    public void init() {
        query = new JPAQueryFactory(em);
        IntStream.rangeClosed(0, 5).forEach(i -> {
            Member member = new Member("member" + i, i);
            memberRepository.save(member);
            Team team = new Team("team" + i);
            teamRepository.save(team);
        });
    }

    @Test
    @Transactional
    @Commit
    public void saveMemberTestWithTeam() {

        // given...
        Member member = new Member("member", 0);
        memberRepository.save(member);
        Team team = new Team("team 1");
        teamRepository.save(team);
        member.addTeam(team);

        // when...
        Member save = memberRepository.save(member);

        // then...
        Assertions.assertThat(save.getName()).isEqualTo(member.getName());
        save.getTeamMembers().forEach(teamMember -> {
            log.info("saved member Name : {}", save.getName());
            log.info("saved team Name : {}", teamMember.getTeam().getName());
            ;
            Assertions.assertThat(teamMember.getTeam().getName()).isEqualTo(team.getName());
        });

    }

    @Test
    public void startJPQL() {

        // given...
        Long memberId = 1L;

        // when...
        String query = "select m from Member m where m.id = :memberId";
        Member member1 = em.createQuery(query, Member.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        // then...
        Assertions.assertThat(member1.getId()).isEqualTo(memberId);

    }


    @Test
    public void startQueryDsl() {

        // given...
        Long memberId = 1L;

        // when...
        Member byId = queryRepository.findById(memberId);

        // then...
        Assertions.assertThat(byId.getId()).isEqualTo(memberId);

    }

    @Test
    public void search() {

        int age = 10;
        String name = "member";
        QMember qMember = member;
        List<Member> fetch = query.select(member)
                .from(member)
                .where(member.age.eq(1), member.name.like("%" + name + "%"))
                .fetch();

        Assertions.assertThat(fetch.size()).isEqualTo(1);
    }


    @Test
    public void supportedMethod() {

        int age = 10;
        String name = "member";
        QMember qMember = member;

        // 1. 단건 조회
        List<Member> fetch = query.select(member)
                .from(member)
                .where(member.age.eq(1), member.name.like("%" + name + "%"))
                .fetch();

        // 2. List 조회
        Member member1 = query.select(member)
                .from(member)
                .where(member.age.eq(1), member.name.like("%" + name + "%"))
                .fetchOne();

        // 3. List 조회와 total count
        QueryResults<Member> memberQueryResults = query.select(member)
                .from(member)
                .where(member.age.eq(1), member.name.like("%" + name + "%"))
                .fetchResults();

        memberQueryResults.getTotal();
        memberQueryResults.getResults();


        // 4. count 만 가져오기
        long l = query.select(member)
                .from(member)
                .where(member.age.eq(1), member.name.like("%" + name + "%"))
                .fetchCount();
    }

    @Test
    public void sort() {
        List<Member> fetch = query.selectFrom(member)
                .where(member.name.contains("member"))
                .orderBy(member.age.desc().nullsLast(), member.name.asc().nullsLast())
                .fetch();


        Assertions.assertThat(fetch.size()).isEqualTo(6);
    }


    @Test
    public void paging() {
        List<Member> fetch = query.selectFrom(member)
                .orderBy(member.age.desc().nullsLast())
                .offset(1)
                .limit(5)
                .fetch();


        Assertions.assertThat(fetch.size()).isEqualTo(5);
        fetch.forEach(member1 -> {
            log.info("age : {}", member1.getAge());
        });

    }

    @Test
    public void pagingWithTotalCount() {
        QueryResults<Member> results = query.selectFrom(member)
                .orderBy(member.age.desc().nullsLast())
                .offset(1)
                .limit(5)
                .fetchResults();

        log.info("size : {}", results.getTotal()); // 전체 레코드 수
        Assertions.assertThat(results.getTotal()).isEqualTo(6);
        results.getResults().forEach(member1 -> {
            log.info("age : {}", member1.getAge());
            log.info("name : {}", member1.getName());

        });

    }

}