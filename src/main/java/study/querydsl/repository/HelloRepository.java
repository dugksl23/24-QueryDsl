package study.querydsl.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.querydsl.entity.Hello;

@Repository
public interface HelloRepository extends JpaRepository<Hello, Long> {

}
