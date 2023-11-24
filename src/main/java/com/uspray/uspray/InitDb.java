package com.uspray.uspray;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.History;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void dbInit() {
            Member member = Member.builder()
                .userId("test")
                .password(passwordEncoder.encode("test"))
                .name("홍길동")
                .phone("01012345678")
                .birth("2002-02-01")
                .gender("female")
                .authority(Authority.ROLE_USER)
                .build();
            em.persist(member);

            Category category = Category.builder()
                .name("기타 카테고리")
                .color("#FFFFFF")
                .member(member)
                .order(1024)
                .build();
            em.persist(category);

            Category category1 = Category.builder()
                .name("가족")
                .color("#408CFF")
                .member(member)
                .order(2048)
                .build();
            em.persist(category1);

            Pray pray = Pray.builder()
                .content("테스트 기도")
                .deadline(LocalDate.parse("2025-01-01"))
                .member(member)
                .category(category)
                .prayType(PrayType.PERSONAL)
                .build();
            em.persist(pray);

            Pray pray1 = Pray.builder()
                .content("기도합니다")
                .deadline(LocalDate.parse("2025-02-24"))
                .member(member)
                .category(category)
                .prayType(PrayType.SHARED)
                .build();
            em.persist(pray1);

            Pray pray2 = Pray.builder()
                .content("기도할게요")
                .deadline(LocalDate.parse("2024-02-24"))
                .member(member)
                .category(category1)
                .prayType(PrayType.SHARED)
                .build();
            em.persist(pray2);

            Pray pray3 = Pray.builder()
                .content("기도할게요")
                .deadline(LocalDate.parse("2024-02-02"))
                .member(member)
                .category(category1)
                .prayType(PrayType.PERSONAL)
                .build();
            em.persist(pray3);

            History history = History.builder()
                .pray(pray)
                .build();
            em.persist(history);
        }

    }
}
