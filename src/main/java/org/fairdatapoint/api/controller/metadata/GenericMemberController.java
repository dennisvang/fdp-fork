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
package org.fairdatapoint.api.controller.metadata;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.member.MemberCreateDTO;
import org.fairdatapoint.api.dto.member.MemberDTO;
import org.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.fairdatapoint.entity.metadata.Metadata;
import org.fairdatapoint.service.member.MemberService;
import org.fairdatapoint.service.metadata.common.MetadataService;
import org.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.fairdatapoint.service.metadata.factory.MetadataServiceFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.fairdatapoint.entity.metadata.MetadataGetter.getMetadataIdentifier;
import static org.fairdatapoint.util.HttpUtil.getMetadataIRI;

@Tag(name = "Authentication and Authorization")
@RestController
@RequiredArgsConstructor
public class GenericMemberController {

    private final String persistentUrl;

    private final MemberService memberService;

    private final MetadataServiceFactory metadataServiceFactory;

    @Operation(hidden = true)
    @GetMapping(path = "{urlPrefix:[^.]+}/{recordId:[^.]+}/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDTO>> getMembers(
            @PathVariable final String urlPrefix,
            @PathVariable final String recordId
    ) throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model metadata = metadataService.retrieve(entityUri);

        // 3. Get members
        final String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        final List<MemberDTO> dto = memberService.getMembers(entityId, Metadata.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(hidden = true)
    @PutMapping(
            path = "{urlPrefix:[^.]+}/{recordId:[^.]+}/members/{userUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MemberDTO> putMember(
            @PathVariable final String urlPrefix,
            @PathVariable final String recordId,
            @PathVariable final UUID userUuid,
            @RequestBody @Valid MemberCreateDTO reqBody
    ) throws MetadataServiceException {
        // 1. Init
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model metadata = metadataService.retrieve(entityUri);

        // 3. Create / Update member
        final String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        final MemberDTO dto = memberService.createOrUpdateMember(entityId, Metadata.class, userUuid,
                UUID.fromString(reqBody.getMembershipUuid()));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(hidden = true)
    @DeleteMapping(path = "{urlPrefix:[^.]+}/{recordId:[^.]+}/members/{userUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMember(
            @PathVariable final String urlPrefix,
            @PathVariable final String recordId,
            @PathVariable final UUID userUuid
    ) throws ResourceNotFoundException, MetadataServiceException {
        // 1. Init
        final MetadataService metadataService = metadataServiceFactory.getMetadataServiceByUrlPrefix(urlPrefix);

        // 2. Get and check existence entity
        final IRI entityUri = getMetadataIRI(persistentUrl, urlPrefix, recordId);
        final Model metadata = metadataService.retrieve(entityUri);

        // 3. Delete member
        final String entityId = getMetadataIdentifier(metadata).getIdentifier().getLabel();
        memberService.deleteMember(entityId, Metadata.class, userUuid);
        return ResponseEntity.noContent().build();
    }
}
