package com.interviewlab.wikimediaconsumer;
import com.interviewlab.wikimediaconsumer.persistence.*;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest; import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:wikimedia;MODE=MySQL;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
class WikimediaEventRepositoryTest {
    @Autowired WikimediaEventRepository repository;
    @Test void guarda_el_payload_completo() { WikimediaEvent saved = repository.save(new WikimediaEvent("page-1", "{\"title\":\"Kafka\"}")); assertThat(saved.getId()).isNotNull(); assertThat(repository.findById(saved.getId())).isPresent(); }
}
