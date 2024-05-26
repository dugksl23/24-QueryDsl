package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter(AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    public Team(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TeamMember> teamMembers = new ArrayList<>();

    public void addMember(Member member){
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(this);
        this.setTeamMembers(teamMembers);
        teamMember.setMember(member);
    }
}
