package study.querydsl.repository;

import org.springframework.stereotype.Repository;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberTeamDto> searchByMemberTeamDto(MemberSearchCondition condition);
    List<Member> searchByMember(MemberSearchCondition condition);

}
