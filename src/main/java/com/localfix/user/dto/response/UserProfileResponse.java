    package com.localfix.user.dto.response;

    import com.localfix.common.enums.AccountStatus;
    import lombok.Builder;

    import java.util.Set;
    import java.util.UUID;

    @Builder
    public record UserProfileResponse(

            UUID id,

            String firstName,

            String lastName,

            String email,

            String mobile,

            AccountStatus accountStatus,

            Boolean emailVerified,

            Boolean mobileVerified,

            Set<String> roles

    ) {
    }