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
package org.fairdatapoint.api.controller.index;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.api.dto.index.ping.PingDTO;
import org.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.fairdatapoint.entity.index.event.IndexEvent;
import org.fairdatapoint.service.UtilityService;
import org.fairdatapoint.service.index.entry.IndexEntryService;
import org.fairdatapoint.service.index.event.EventService;
import org.fairdatapoint.service.index.webhook.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Index")
@Slf4j
@RestController
@RequestMapping("/index/admin")
@RequiredArgsConstructor
public class IndexAdminController {

    private final UtilityService utilityService;

    private final EventService eventService;

    private final WebhookService webhookService;

    private final IndexEntryService indexEntryService;

    @Operation(hidden = true)
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerMetadataRetrieve(
            @RequestBody @Valid PingDTO reqDto,
            HttpServletRequest request
    ) throws MetadataRepositoryException {
        log.info("Received ping trigger request from {}",
                utilityService.getRemoteAddr(request));
        final IndexEvent event = eventService.acceptAdminTrigger(request, reqDto);
        webhookService.triggerWebhooks(event);
        eventService.triggerMetadataRetrieval(event);
        indexEntryService.harvest(reqDto.getClientUrl());
    }

    @Operation(hidden = true)
    @PostMapping("/trigger-all")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerMetadataRetrieveAll(HttpServletRequest request) {
        log.info("Received ping trigger all request from {}",
                utilityService.getRemoteAddr(request));
        final IndexEvent event = eventService.acceptAdminTriggerAll(request);
        webhookService.triggerWebhooks(event);
        eventService.triggerMetadataRetrieval(event);
    }

    @Operation(hidden = true)
    @PostMapping("/ping-webhook")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void webhookPing(@RequestParam UUID webhook, HttpServletRequest request) {
        log.info("Received webhook {} ping trigger from {}",
                webhook, utilityService.getRemoteAddr(request));
        final IndexEvent event = webhookService.handleWebhookPing(request, webhook);
        webhookService.triggerWebhooks(event);
    }
}
