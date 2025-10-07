package rootmaker.rootmakerbackend.domain.user;

import jakarta.persistence.*;
import lombok.*;

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

    private String userType; // 프론트엔드에서 분석한 사용자 유형 (예: PINE, BAMBOO)
}
