package study.querydsl.controller;


import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamRepository;

import java.util.stream.IntStream;

@Profile("test")
@Slf4j
@Component
@RequiredArgsConstructor
public class InitMemberTest {

    private final createMemberInit createMemberInit;

    @PostConstruct
    public void init() {

        createMemberInit.init();
    }


    @Transactional(readOnly = true)
    @RequiredArgsConstructor
    @Component
    static class createMemberInit {

        private final MemberRepository memberRepository;
        private final TeamRepository teamRepository;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            Team save = teamRepository.save(teamA);
            Team save1 = teamRepository.save(teamB);

            IntStream.rangeClosed(1, 50).forEach(i -> {
                Member member = new Member("member" + i, i);
                memberRepository.save(member);
                if (i % 2 == 0) {
                    member.addTeam(save1); // 짝수는 teamB와 연관관계 설정
                } else {
                    member.addTeam(save); // 홀수는 teamA와 연관관계 설정
                }
            });

            long count = memberRepository.count();
            log.info("member init completed successfully, count : {}", count);

            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            if (isActive) {
                log.info("active : {}", isActive);
            } else {
                log.info("not active : {}", isActive);
            }


        }

    }


}
