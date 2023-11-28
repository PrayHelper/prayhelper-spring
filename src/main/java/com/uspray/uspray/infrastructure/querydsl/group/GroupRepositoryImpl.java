package com.uspray.uspray.infrastructure.querydsl.group;

import static com.uspray.uspray.domain.QGroup.group;
import static com.uspray.uspray.domain.QMember.member;
import static com.uspray.uspray.domain.QGroupPray.groupPray;
import static com.uspray.uspray.domain.QGroupMember.groupMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.DTO.auth.response.QMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.DTO.group.response.QGroupResponseDto;
import com.uspray.uspray.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupResponseDto> findGroupListByMember(Member target) {
        return queryFactory
            .select(new QGroupResponseDto(
                group.id,
                group.name,
                groupPray.content.max(),
                group.groupMemberList.size(),
                group.groupPrayList.size(),
                groupPray.createdAt.max()
            ))
            .from(group)
            .join(group.groupMemberList, groupMember)
            .leftJoin(group.groupPrayList, groupPray)
            .where(groupMember.member.eq(target))
            .groupBy(group.id)
            .fetch();
    }

    @Override
    public List<MemberResponseDto> findGroupMembersByGroupAndNameLike(Long groupId, String name) {
        return queryFactory
            .select(new QMemberResponseDto(
                member.userId,
                member.name,
                member.phone
            ))
            .from(member)
            .join(member.groupMemberList, groupMember)
            .where(groupMember.group.id.eq(groupId)
                .and(member.name.likeIgnoreCase("%" + name + "%")))
            .fetch();
    }

    @Override
    public List<MemberResponseDto> findGroupMembers(Long groupId) {
        return queryFactory
            .select(new QMemberResponseDto(
                member.userId,
                member.name,
                member.phone
            ))
            .from(member)
            .join(member.groupMemberList, groupMember)
            .where(groupMember.group.id.eq(groupId))
            .fetch();
    }
}
