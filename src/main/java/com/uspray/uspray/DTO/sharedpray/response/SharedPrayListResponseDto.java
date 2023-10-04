package com.uspray.uspray.DTO.sharedpray.response;

import com.uspray.uspray.DTO.pray.PrayDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedPrayListResponseDto {

    private List<PrayDto> sharedPrayList;
}
