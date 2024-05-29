package study.querydsl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberTeamDto> search(MemberSearchCondition memberSearchCondition) {
        return memberRepository.searchByMemberTeamDto(memberSearchCondition);
    }


    public Page<MemberTeamDto> searchPageSimple(int offset, int limit, MemberSearchCondition memberSearchCondition){
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "id");
        return memberRepository.searchPageSimple(memberSearchCondition, pageRequest);
    }

    public Page<MemberTeamDto> searchPageComplex(int offset, int limit, MemberSearchCondition memberSearchCondition){
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "id");
        return memberRepository.searchPageSimple(memberSearchCondition, pageRequest);
    }
}
