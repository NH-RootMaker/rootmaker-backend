package rootmaker.rootmakerbackend.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dependent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String relationship;
    private String birthDate;
    private boolean cohabiting;

    @Builder
    public Dependent(User user, String relationship, String birthDate, boolean cohabiting) {
        this.user = user;
        this.relationship = relationship;
        this.birthDate = birthDate;
        this.cohabiting = cohabiting;
    }

    // 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
    }
}
