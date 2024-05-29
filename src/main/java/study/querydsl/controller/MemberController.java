package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import study.querydsl.dto.ApiResultResponse;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberRepository;
import study.querydsl.service.MemberService;

import java.util.List;

@RestController
@Transactional(readOnly = true)
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/v1/search")
    public ApiResultResponse searchMemberByCond(MemberSearchCondition memberSearchCondition){
        log.info("들어오나요?");
        List<MemberTeamDto> search = memberService.search(memberSearchCondition);
        return new ApiResultResponse(search.size(), search);
    }


}
