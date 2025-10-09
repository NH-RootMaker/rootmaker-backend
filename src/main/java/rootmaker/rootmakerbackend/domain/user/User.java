package rootmaker.rootmakerbackend.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 가상 사용자 이름

    // ML 서버 요청에 필요한 프로필 정보
    private String ageBand;
    private String regionCode;
    private String incomeBand;
    private String typeCode;
    private Integer payday;

    // --- 청약 가점 계산용 상세 정보 (nullable) ---
    private String birthDate;
    private String maritalStatus;
    private String marriageDate;
    private String homelessStartDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Dependent> dependents = new ArrayList<>();

    private String userType; // 프론트엔드에서 분석한 사용자 유형 (예: PINE, BAMBOO)

    // 연관관계 편의 메서드
    public void addDependent(Dependent dependent) {
        dependents.add(dependent);
        dependent.setUser(this);
    }

    public void clearDependents() {
        this.dependents.clear();
    }
}
