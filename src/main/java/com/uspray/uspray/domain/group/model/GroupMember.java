package com.uspray.uspray.domain.group.model;

import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.domain.member.model.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class GroupMember extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupmember_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Boolean notificationAgree = true;

    public static GroupMember of(Group group, Member member) {
        return GroupMember.builder()
            .group(group)
            .member(member)
            .build();
    }

    @Builder
    public GroupMember(Group group, Member member) {
        setGroup(group);
        setMember(member);
    }

    public void setNotificationAgree(Boolean notificationAgree) {
        this.notificationAgree = notificationAgree;
    }

    private void setMember(Member member) {
        this.member = member;
        member.getGroupMemberList().add(this);
    }

    private void setGroup(Group group) {
        this.group = group;
        group.getGroupMemberList().add(this);
    }
}
