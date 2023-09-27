package com.uspray.uspray.domain;

import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE pray SET deleted = true WHERE pray_id = ?")
@Where(clause = "deleted=false")
public class Pray extends AuditingTimeEntity {

  @Id
  @GeneratedValue
  @Column(name = "pray_id")
  private Long id;
  @ManyToOne
  @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
  private Member member;

  private String content;

  @ColumnDefault("0")
  private Integer count;

  private LocalDate deadline;

  private final Boolean deleted = false;

  @Builder
  public Pray(Member member, String content, LocalDate deadline) {
    this.member = member;
    this.content = content;
    this.count = 0;
    this.deadline = deadline;
  }

  public void update(PrayRequestDto prayRequestDto) {
    this.content = prayRequestDto.getContent();
    this.deadline = prayRequestDto.getDeadline();
  }
}
