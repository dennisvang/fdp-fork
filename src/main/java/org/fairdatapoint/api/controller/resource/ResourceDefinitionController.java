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
package org.fairdatapoint.api.controller.resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionDTO;
import org.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.fairdatapoint.service.resource.ResourceDefinitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Tag(name = "Metadata Model")
@RestController
@RequestMapping("/resource-definitions")
@RequiredArgsConstructor
public class ResourceDefinitionController {

    private static final String NOT_FOUND_MSG = "Resource Definition '%s' doesn't exist";

    private final ResourceDefinitionService resourceDefinitionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceDefinitionDTO>> getResourceDefinitions() {
        final List<ResourceDefinitionDTO> dto = resourceDefinitionService.getAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> createResourceDefinitions(
            @RequestBody @Valid ResourceDefinitionChangeDTO reqDto
    ) throws BindException {
        final ResourceDefinitionDTO dto = resourceDefinitionService.create(reqDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> getResourceDefinition(
            @PathVariable final UUID uuid
    ) throws ResourceNotFoundException {
        final Optional<ResourceDefinitionDTO> oDto = resourceDefinitionService.getDTOByUuid(uuid);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @PutMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDefinitionDTO> putResourceDefinitions(
            @PathVariable final UUID uuid,
            @RequestBody @Valid ResourceDefinitionChangeDTO reqDto
    ) throws ResourceNotFoundException, BindException {
        final Optional<ResourceDefinitionDTO> oDto = resourceDefinitionService.update(uuid, reqDto);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }

    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteResourceDefinitions(@PathVariable final UUID uuid)
            throws ResourceNotFoundException {
        final boolean result = resourceDefinitionService.deleteByUuid(uuid);
        if (result) {
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ResourceNotFoundException(format(NOT_FOUND_MSG, uuid));
        }
    }
}
