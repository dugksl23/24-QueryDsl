package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
import study.querydsl.repository.TeamRepository;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;
import static study.querydsl.entity.QTeamMember.teamMember;


@SpringBootTest
@Slf4j
public class quertDslBasicTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberQueryRepository queryRepository;

    @Autowired
    private EntityManager em;
    @Autowired
    private EntityManagerFactory emf;

    private JPAQueryFactory query;

    @BeforeEach
    public void MemberTest() {
        query = new JPAQueryFactory(em);
    }

    //    @BeforeEach
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
        assertThat(save.getName()).isEqualTo(member.getName());
        save.getTeamMembers().forEach(teamMember -> {
            log.info("saved member Name : {}", save.getName());
            log.info("saved team Name : {}", teamMember.getTeam().getName());
            ;
            assertThat(teamMember.getTeam().getName()).isEqualTo(team.getName());
        });

    }

    @Test
    public void startJPQL() {

        // given...
        Long memberId = 1L;

        // when...
        String query = "select m from Member m where m.id = :memberId";
        Member member1 = em.createQuery(query, Member.class).setParameter("memberId", memberId).getSingleResult();

        // then...
        assertThat(member1.getId()).isEqualTo(memberId);

    }


    @Test
    public void startQueryDsl() {

        // given...
        Long memberId = 1L;

        // when...
        Member byId = queryRepository.findById(memberId);

        // then...
        assertThat(byId.getId()).isEqualTo(memberId);

    }

    @Test
    public void search() {

        int age = 10;
        String name = "member";
        QMember qMember = member;
        List<Member> fetch = query.select(member).from(member).where(member.age.eq(1), member.name.like("%" + name + "%")).fetch();

        assertThat(fetch.size()).isEqualTo(1);
    }


    @Test
    public void supportedMethod() {

        int age = 10;
        String name = "member";
        QMember qMember = member;

        // 1. 단건 조회
        List<Member> fetch = query.select(member).from(member).where(member.age.eq(1), member.name.like("%" + name + "%")).fetch();

        // 2. List 조회
        Member member1 = query.select(member).from(member).where(member.age.eq(1), member.name.like("%" + name + "%")).fetchOne();

        // 3. List 조회와 total count
        QueryResults<Member> memberQueryResults = query.select(member).from(member).where(member.age.eq(1), member.name.like("%" + name + "%")).fetchResults();

        memberQueryResults.getTotal();
        memberQueryResults.getResults();


        // 4. count 만 가져오기
        long l = query.select(member).from(member).where(member.age.eq(1), member.name.like("%" + name + "%")).fetchCount();
    }

    @Test
    public void sort() {
        List<Member> fetch = query.selectFrom(member).where(member.name.contains("member")).orderBy(member.age.desc().nullsLast(), member.name.asc().nullsLast()).fetch();


        assertThat(fetch.size()).isEqualTo(6);
    }


    @Test
    public void paging() {
        List<Member> fetch = query.selectFrom(member).orderBy(member.age.desc().nullsLast()).offset(1).limit(5).fetch();


        assertThat(fetch.size()).isEqualTo(5);
        fetch.forEach(member1 -> {
            log.info("age : {}", member1.getAge());
        });

    }

    @Test
    public void pagingWithTotalCount() {
        QueryResults<Member> results = query.selectFrom(member).orderBy(member.age.desc().nullsLast()).offset(1).limit(5).fetchResults();

        log.info("size : {}", results.getTotal()); // 전체 레코드 수
        assertThat(results.getTotal()).isEqualTo(6);
        results.getResults().forEach(member1 -> {
            log.info("age : {}", member1.getAge());
            log.info("name : {}", member1.getName());

        });

    }


    @Test
    public void aggregation() {
        List<Tuple> fetch = query.select(member.age.avg(), teamMember.team.name).from(member).join(member.teamMembers, teamMember).join(teamMember.team, team).groupBy(team.name).having(member.age.gt(0)).fetch();

        Double v = fetch.get(0).get(0, Double.class);
        String s = fetch.get(0).get(1, String.class);

        log.info("age : {}", v);
        log.info("team name : {}", s);

        assertThat(fetch.size()).isEqualTo(5);
        assertThat(v).isEqualTo(1);
        assertThat(s).isEqualTo("team1");

    }


    /**
     * 팀1에 소속된 모든 회원을 찾아라.
     */
    @Test
    @Transactional
    public void join() {

        String teamName = "team1";
        List<Member> fetch = query.select(member).from(member).where(team.name.eq(teamName)).join(member.teamMembers, teamMember).join(teamMember.team, team).fetch();

        log.info("size : {}", fetch.size());
        assertThat(fetch.get(0).getTeamMembers().get(0).getTeam().getName()).isEqualTo(teamName);
        fetch.stream().forEach(member1 -> {
            log.info("name : {}", member1.getName());
        });
    }

    /**
     * 팀1에 소속된 모든 회원을 찾아라.
     */
    @Test
    @Transactional
    public void theta_join() {

        Member team1 = new Member("team1", 0);
        Member team2 = new Member("team1", 0);
        memberRepository.save(team1);
        memberRepository.save(team2);

        String teamName = "team1";
        List<Member> fetch = query.select(member).from(member, team).where(member.name.eq(teamName)).fetch();

        log.info("size : {}", fetch.size());
        assertThat(fetch.size()).isEqualTo(2);
        fetch.stream().forEach(member1 -> {
            log.info("member name : {}", member1.getName());
            ;
        });
    }

    /**
     * Ex) 회원과 팀을 조인하면서, 팀 이름이 team1  팀만 조인하며, 회원은 모두 조회
     */
    @Test
    @Transactional
    public void leftJoin_on() {

        // Given: 팀 이름
        Member member1 = new Member("team1", 0);
        Member member2 = new Member("team1", 1);
        Member member3 = new Member("team1", 2);
        memberRepository.save(member2);
        memberRepository.save(member1);
        memberRepository.save(member3);

        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        Team team3 = new Team("team3");
        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);

        member1.addTeam(team1);
        member2.addTeam(team2);
        member3.addTeam(team3);

        // Given: 팀 이름
        String teamName = "team1";

        // When: QueryDSL로 회원을 조회하면서 특정 팀 이름을 가진 팀과 LEFT JOIN
        List<Member> fetch = query.selectFrom(member).distinct()
                .leftJoin(member.teamMembers, teamMember)
                .leftJoin(teamMember.team, team)
                .on(teamMember.team.name.eq(team1.getName()))
//                .join(member.teamMembers, teamMember)
//                .join(teamMember.team, team)
//                .where(team.name.eq(teamName))
                .fetch();

        // Then: 조회된 회원 정보를 출력하고 검증
        fetch.forEach(m -> {
            log.info("member id : {}", m.getId());
            log.info("name : {}", m.getName());
            log.info("age : {}", m.getAge());
            m.getTeamMembers().forEach(tm -> {
                log.info("team name : {}", tm.getTeam().getName());
            });
        });

        // Assert: 조회된 회원의 수를 검증
        assertThat(fetch.size()).isEqualTo(3);
    }

    /**
     * Ex) 연관관계 없는 엔티티 외부 조인
     * 회원의 이름이 팀 이름과 같은 대상 외부 조인
     */
    @Test
    @Transactional
    public void leftJoin_on_noRelation() {
        // Given: 팀 이름
        memberRepository.save(new Member("team1", 0));
        memberRepository.save(new Member("team1", 1));
        memberRepository.save(new Member("team1", 2));

        teamRepository.save(new Team("team1"));
        teamRepository.save(new Team("team2"));
        teamRepository.save(new Team("team3"));

        String teamName = "team1";

        // When: QueryDSL로 회원을 조회하면서 특정 팀 이름을 가진 팀과 LEFT JOIN
        List<Tuple> fetch = query
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.name.eq(team.name))
                .fetch();

        for (Tuple tuple : fetch) {
            ;
            log.info("tuple : {}", tuple);
        }

        // Assert: 조회된 회원의 수를 검증
        assertThat(fetch.size()).isEqualTo(3);
    }

    /**
     * Fetch join
     */
    @Test
    @Transactional
    public void fetch_Join() {

        // When: QueryDSL로 회원을 조회하면서 특정 팀 이름을 가진 팀과 LEFT JOIN
        List<Member> fetch = query.select(member).from(member).join(member.teamMembers, teamMember).fetchJoin().fetch();

        // Then: 조회된 회원 정보를 출력하고 검증
        fetch.forEach(m -> {
            log.info("member id : {}", m.getId());
            log.info("name : {}", m.getName());
            log.info("age : {}", m.getAge());
            m.getTeamMembers().forEach(tm -> {
                boolean loaded = emf.getPersistenceUnitUtil().isLoaded(tm.getTeam());
                assertThat(loaded).as("페치 조인 적용").isTrue();
                log.info("team name : {}", tm.getTeam().getName());
            });
        });

        // Assert: 조회된 회원의 수를 검증
        assertThat(fetch.size()).isEqualTo(5);

    }

    /**
     * 서브 쿼리
     * 나이 많은 사람 조회
     */
    @Test
    public void subQuery() {

        QMember subMember = new QMember("subMember");
        List<Member> fetch = query.select(member).from(member).where(member.age.eq(JPAExpressions.select(subMember.age.max()).from(subMember))).fetch();

        Assertions.assertThat(fetch.size()).isEqualTo(1);
        fetch.forEach(m -> log.info("member age : {}", m.getAge()));
    }

    /**
     * 나이가 평균 이상인 회원
     */
    @Test
    public void subQueryGoe() {

        QMember subMember = new QMember("subMember");
        List<Member> fetch = query.select(member).from(member).where(member.age.goe(JPAExpressions.select(subMember.age.avg()).from(subMember))).fetch();

        fetch.forEach(m -> log.info("member age : {}", m.getAge()));
    }


    /**
     * 서브 쿼리 여러 건 처리, in
     * 회원의 전체 평균 나이에 들어오는 모든 회원,
     */
    @Test
    public void subQueryIn() {

        QMember subMember = new QMember("subMember");
        List<Member> fetch = query.select(member)
                .from(member)
                .where(member.age.in
                        (JPAExpressions
                                .select(subMember.age)
                                .from(subMember)
                                .where(subMember.age.goe(2))
                        )).fetch();

        fetch.forEach(m -> log.info("member age : {}", m.getAge()));
    }

    /**
     * 서브 쿼리 여러 건 처리, in
     * 회원의 전체 평균 나이에 들어오는 모든 회원,
     */
    @Test
    public void case_simple() {

        List<String> cas = query.select(member.age
                        .when(1).then("1살 이상")
                        .when(5).then("5살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        cas.forEach(m -> log.info("member age : {}", m));
    }

    /**
     * 복잡한 조건
     */
    @Test
    public void case_complex() {

        StringExpression caseExpression = new CaseBuilder()
                .when(member.age.between(1, 2)).then("2살 이하")
                .when(member.age.between(3, 5)).then("5살 이하")
                .otherwise("기타");
        List<String> fetch = query.select(caseExpression)
                .from(member)
                .fetch();

        fetch.forEach(m -> log.info("member age : {}", m));
    }


    /**
     * 복잡한 조건 order by
     */
    @Test
    public void case_orderBy() {

        StringExpression caseExpression = new CaseBuilder()
                .when(member.age.between(1, 2)).then("2살 이하")
                .when(member.age.between(3, 5)).then("5살 이하")
                .otherwise("기타");
        List<Member> fetch = query.select(member)
                .from(member)
                .orderBy(caseExpression.desc())
                .fetch();

        fetch.forEach(m -> log.info("member age : {}", m));
    }


    /**
     * 상수가 필요할 경우
     */
    @Test
    public void constant() {

        List<Tuple> fetch = query.select(
                        member.name,
                        Expressions.constant("안녕"))
                .from(member)
                .fetch();
    }


    /**
     * 문자 더하기
     * 유저 내임과 나이 더하기
     */
    @Test
    public void concat() {

        List<String> fetch = query.select(
                        member.name.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        fetch.forEach(m -> log.info("member name : {}", m));
    }

}