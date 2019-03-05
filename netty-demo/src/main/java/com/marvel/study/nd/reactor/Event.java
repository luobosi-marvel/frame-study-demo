/*
 * Copyright (C) 2009-2018 Hangzhou 2Dfire Technology Co., Ltd. All rights reserved
 */
package com.marvel.study.nd.reactor;

import com.luobosi.net.AccountDTO;
import lombok.Data;

/**
 * Event
 * reactor 模式中内部处理的 event 类
 *
 * @author luobosi@2dfire.com
 * @since 2019-01-21
 */
@Data
public class Event {

    private InputSource<AccountDTO> inputSource;

    private EventType type;
}
