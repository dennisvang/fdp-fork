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
package org.fairdatapoint.service.member;

import lombok.RequiredArgsConstructor;
import org.fairdatapoint.api.dto.member.MemberDTO;
import org.fairdatapoint.entity.membership.Membership;
import org.fairdatapoint.entity.user.UserAccount;
import org.fairdatapoint.service.membership.MembershipMapper;
import org.fairdatapoint.service.user.UserMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final MembershipMapper membershipMapper;

    private final UserMapper userMapper;

    public MemberDTO toDTO(UserAccount user, Membership membership) {
        return
                new MemberDTO(
                        userMapper.toSimpleDTO(user),
                        membershipMapper.toDTO(membership));
    }

}
