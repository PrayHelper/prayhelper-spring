package com.uspray.uspray.DTO.group.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupKickRequestDto {

    @NotNull
    private String username;
}
