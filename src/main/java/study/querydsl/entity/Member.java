package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    private int age;

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();


    //연관관계 편의 메서드
    public void addTeam(Team team){
        TeamMember teamMember = new TeamMember();
        teamMember.setMember(this);
        teamMember.setTeam(team);
        this.teamMembers.add(teamMember);
    }
}
