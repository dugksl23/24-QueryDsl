package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.test.annotation.Commit;

@SpringBootTest
class QuerydslApplicationTests {

    private final EntityManager em;
    private final JPAQueryFactory query;

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
//    @Commit
    void queryDsl() {

		// given...
        Hello hello1 = new Hello("hello");
        em.persist(hello1);
        QHello hello2 = new QHello("hello");
        QHello qHello = QHello.hello;

		// when...
		Hello hello = query.select(qHello)
				.from(qHello)
				.where(qHello.name.eq("hello"))
				.orderBy(qHello.name.asc())
				.fetchOne();

		// then...
		Assertions.assertThat(hello).isEqualTo(hello1);
        Assertions.assertThat(hello.getId()).isEqualTo(hello1.getId() );



    }

}
