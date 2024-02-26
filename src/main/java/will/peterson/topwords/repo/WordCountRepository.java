package will.peterson.topwords.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import will.peterson.topwords.entity.WordCount;

public interface WordCountRepository extends JpaRepository<WordCount, String> {
}

