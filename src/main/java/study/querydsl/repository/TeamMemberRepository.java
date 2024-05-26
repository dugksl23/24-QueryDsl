package study.querydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.TeamMember;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
