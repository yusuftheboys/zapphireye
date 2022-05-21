package com.zapphireye.zapphireye.model.database.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Risk {
    private ThreatAgent threatAgent;
    private Vulnerability vulnerability;
    private Technical technical;
    private Business business;
    private float total;
    private String rate;
}
