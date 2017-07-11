package org.meeuw.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Data
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class ESObject {

    private String id;
    private String type;
    private Double score;
    private Map<String, Object> source;
}
