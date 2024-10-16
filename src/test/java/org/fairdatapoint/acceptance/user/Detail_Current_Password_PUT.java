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
package org.fairdatapoint.acceptance.user;

import org.fairdatapoint.WebIntegrationTest;
import org.fairdatapoint.api.dto.user.UserDTO;
import org.fairdatapoint.api.dto.user.UserPasswordDTO;
import org.fairdatapoint.database.db.repository.UserAccountRepository;
import org.fairdatapoint.entity.user.UserAccount;
import org.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /users/current/password")
public class Detail_Current_Password_PUT extends WebIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private URI url() {
        return URI.create("/users/current/password");
    }

    private UserPasswordDTO reqDto() {
        return new UserPasswordDTO("newPassword");
    }


    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        UserAccount user = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
        RequestEntity<UserPasswordDTO> request = RequestEntity
                .put(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<UserDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<UserDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(user, result.getBody());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPut(client, url(), reqDto());
    }

}
