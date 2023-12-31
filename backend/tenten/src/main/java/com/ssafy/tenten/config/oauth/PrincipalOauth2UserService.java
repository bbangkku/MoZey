package com.ssafy.tenten.config.oauth;

import com.ssafy.tenten.api.repository.UserRepository;
import com.ssafy.tenten.config.auth.PrincipalDetails;
import com.ssafy.tenten.config.oauth.provider.KakaoUserInfo;
import com.ssafy.tenten.config.oauth.provider.OAuth2UserInfo;
import com.ssafy.tenten.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    // 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 메소드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인은 완료 -> code를 return(OAuth-Client라이브러리) -> AccessToken요청
        // userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원 프로필 받아준다

        OAuth2User oAuth2User = super.loadUser(userRequest); // 소셜 로그인의 회원 프로필 조회

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // Attribute를 파싱해서 공통 객체로 묶는다. 관리가 편함.
        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else {
            log.info("카카오 외 다른 플랫폼 지원 x");
        }

        Optional<User> userOptional =
                userRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());

        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = User.builder()
                    .email(oAuth2UserInfo.getEmail())
                    .gender((oAuth2UserInfo.getGender().equals("male")) ? 'M' : 'W')
                    .role("USER")
                    .provider(oAuth2UserInfo.getProvider())
                    .providerId(oAuth2UserInfo.getProviderId())
                    .image((String) ((Map) oAuth2User.getAttributes().get("properties")).get("thumbnail_image"))
                    .build();
        }
        user.setKakaoToken(userRequest.getAccessToken().getTokenValue());
        userRepository.save(user);
        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

}
