package study.querydsl.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberRepository;
import study.querydsl.repository.TeamRepository;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional(readOnly = true)
@Slf4j
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void simplePagingByCondition() {

        // given...
        int offset = 0;
        int limit = 10;
        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setMemberName("member");
        condition.setTeamName("teamA");
        condition.setAgeLoe(10);
        condition.setAgeGoe(1);

        // when...
        Page<MemberTeamDto> memberTeamDtos = memberService.searchPageSimple(offset, limit, condition);

        // then..
        log.info("memberTeamDtos size: {}", memberTeamDtos.getSize());
        memberTeamDtos.getContent().forEach(member ->{
            log.info("member Name : {}", member.getMemberName());
            log.info("team Name : {}", member.getTeamName());
            log.info("member age : {}", member.getAge());
        });
    }

    @Test
    public void complexPagingByCondition() {

        // given...
        int offset = 0;
        int limit = 10;
        MemberSearchCondition condition = new MemberSearchCondition();
//        condition.setMemberName("member");
        condition.setTeamName("teamA");
        condition.setAgeLoe(10);
        condition.setAgeGoe(1);

        // when...
        Page<MemberTeamDto> memberTeamDtos = memberService.searchPageComplex(offset, limit, condition);

        // then..
        log.info("memberTeamDtos size: {}", memberTeamDtos.getSize());
        memberTeamDtos.getContent().forEach(member ->{
            log.info("member Name : {}", member.getMemberName());
            log.info("team Name : {}", member.getTeamName());
            log.info("member age : {}", member.getAge());
        });
    }
}