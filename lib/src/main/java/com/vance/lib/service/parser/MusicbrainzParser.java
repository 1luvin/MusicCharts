package com.vance.lib.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;

import static com.vance.lib.util.JsonUtil.getLong;
import static com.vance.lib.util.JsonUtil.readTree;

public class MusicbrainzParser {

    public Long parseCountOfItems(String info) throws JsonProcessingException {
        return getLong(readTree(info), "count");
    }
}
