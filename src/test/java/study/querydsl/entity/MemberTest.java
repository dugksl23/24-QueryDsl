package study.querydsl.entity;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamMemberRepository;
import study.querydsl.repository.TeamRepository;


@SpringBootTest
@Slf4j
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;


    @Test
    @Transactional
    @Commit
    public void saveMemberTestWithTeam(){

        // given...
        Member member = new Member("member 1", 0);
        memberRepository.save(member);
        Team team = new Team("team1");
        teamRepository.save(team);
        member.addTeam(team);

        // when...
        Member save = memberRepository.save(member);

        // then...
        Assertions.assertThat(save.getName()).isEqualTo(member.getName());
        save.getTeamMembers().forEach(teamMember -> {
            log.info("saved member Name : {}", save.getName());
            log.info("saved team Name : {}", teamMember.getTeam().getName());;
            Assertions.assertThat(teamMember.getTeam().getName()).isEqualTo(team.getName());
        });

    }

}