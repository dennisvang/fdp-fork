/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatapoint.service.index.event;

import org.fairdatapoint.api.dto.index.event.EventDTO;
import org.fairdatapoint.entity.index.event.payload.AdminTrigger;
import org.fairdatapoint.entity.index.event.IndexEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {

    private static final Integer VERSION = 1;

    public EventDTO toDTO(IndexEvent event) {
        return new EventDTO(
                event.getUuid(),
                event.getType(),
                event.getCreatedAt().toString(),
                event.getFinishedAt().toString()
        );
    }

    public IndexEvent toAdminTriggerEvent(
            Authentication authentication, String clientUrl, String remoteAddr
    ) {
        final AdminTrigger adminTrigger = new AdminTrigger();
        adminTrigger.setRemoteAddr(remoteAddr);
        adminTrigger.setTokenName(authentication.getName());
        adminTrigger.setClientUrl(clientUrl);
        return new IndexEvent(VERSION, adminTrigger);
    }

}
