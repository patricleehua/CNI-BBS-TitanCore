package com.titancore.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostFrequencyVo {

    private LocalDate postDate;

    private int postCount;

}
