package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {

    private String name;
    private Integer age;

    @QueryProjection
    public MemberDto(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
