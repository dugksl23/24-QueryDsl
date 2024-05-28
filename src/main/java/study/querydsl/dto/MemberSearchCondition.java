package study.querydsl.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSearchCondition {

    // 회원명, 팀명, 나이(ageGoe, ageLoe)
    private String memberName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;

}
