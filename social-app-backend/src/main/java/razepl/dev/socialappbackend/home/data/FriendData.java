package razepl.dev.socialappbackend.home.data;

import lombok.Builder;

@Builder
public record FriendData(String friendFullName, String friendJob, String friendUsername) {
}
