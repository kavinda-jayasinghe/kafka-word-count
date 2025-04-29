package com.example.word_count.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {
    private int nodeId;
    private String port;
}
