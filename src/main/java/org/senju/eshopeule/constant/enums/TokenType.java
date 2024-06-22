package org.senju.eshopeule.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {
    REFRESH_TOKEN("rt"),
    ACCESS_TOKEN("at");

    private final String typeName;
}
