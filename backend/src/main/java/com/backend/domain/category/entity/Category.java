package com.backend.domain.category.entity;

import com.backend.global.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;

    // category 객체의 값이 동일한지 비교하는 메서드
    // 테스트 중 에러가 발생해서 추가한 메서드
    // 예상 결과와 실제 결과를 비교할 때 사용
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name);
    }

    // equals() 메서드와 함께 사용되는 hashCode() 메서드
    // 이 메서드에서 비교하는 필드들을 기반으로 hashCode 값을 계산, 객체 비교의 일관성을 유지
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
