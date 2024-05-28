package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;
import study.querydsl.repository.HelloRepository;

@SpringBootTest
@Slf4j
class QuerydslApplicationTests {

    private final EntityManager em;
    private final JPAQueryFactory query;
    @Autowired
    private HelloRepository helloRepository;

    @Autowired
    public QuerydslApplicationTests(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Test
    void contextLoads() {
    }

    @Test
    @Transactional
    void queryDsl() {

		// given...
        Hello hello1 = new Hello("hello");
        Hello save = helloRepository.save(hello1);
        QHello hello2 = new QHello("hello");
        QHello qHello = QHello.hello;

		// when...
		Hello hello = query.select(qHello)
				.from(qHello)
				.where(qHello.name.eq("hello"))
				.orderBy(qHello.name.asc())
				.fetchOne();

		// then...
        log.info("created hello: {}", hello.getCreatedAt());
		Assertions.assertThat(hello).isEqualTo(save);
        Assertions.assertThat(hello.getId()).isEqualTo(save.getId() );

    }

}
