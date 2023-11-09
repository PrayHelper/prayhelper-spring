package com.uspray.uspray.DTO.history.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryListResponseDto {

    private List<HistoryResponseDto> historyList;

    private Integer totalPage;

}
