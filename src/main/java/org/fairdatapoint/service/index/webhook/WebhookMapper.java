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
package org.fairdatapoint.service.index.webhook;

import org.fairdatapoint.api.dto.index.webhook.WebhookPayloadDTO;
import org.fairdatapoint.entity.index.event.IndexEvent;
import org.fairdatapoint.entity.index.event.payload.WebhookPing;
import org.fairdatapoint.entity.index.event.payload.WebhookTrigger;
import org.fairdatapoint.entity.index.webhook.IndexWebhook;
import org.fairdatapoint.entity.index.webhook.IndexWebhookEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class WebhookMapper {

    private static final Integer VERSION = 1;

    public IndexEvent toTriggerEvent(IndexWebhook webhook, IndexWebhookEvent webhookEvent, IndexEvent triggerEvent) {
        final WebhookTrigger webhookTrigger = new WebhookTrigger();
        webhookTrigger.setWebhook(webhook);
        webhookTrigger.setMatchedEvent(webhookEvent);
        return new IndexEvent(VERSION, webhookTrigger, triggerEvent);
    }

    public IndexEvent toPingEvent(
            Authentication authentication, UUID webhookUuid, String remoteAddr
    ) {
        final WebhookPing webhookPing = new WebhookPing();
        webhookPing.setWebhookUuid(webhookUuid);
        webhookPing.setRemoteAddr(remoteAddr);
        webhookPing.setTokenName(authentication.getName());
        return new IndexEvent(VERSION, webhookPing);
    }

    public WebhookPayloadDTO toWebhookPayloadDTO(IndexEvent event) {
        final WebhookPayloadDTO webhookPayload = new WebhookPayloadDTO();
        webhookPayload.setEvent(event.getPayload().getWebhookTrigger().getMatchedEvent());
        webhookPayload.setClientUrl(event.getRelatedTo().getClientUrl());
        webhookPayload.setSecret(event.getPayload().getWebhookTrigger().getWebhook().getSecret());
        webhookPayload.setUuid(event.getUuid().toString());
        webhookPayload.setTimestamp(Instant.now().toString());
        return webhookPayload;
    }

}
