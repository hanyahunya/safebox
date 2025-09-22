package com.safebox.back.rpi.service;

import com.safebox.back.mail.MailService;
import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.dto.RpiParcelUuidDto;
import com.safebox.back.rpi.entity.Rpi;
import com.safebox.back.rpi.repository.RpiRepository;
import com.safebox.back.rpi.util.TotpService;
import com.safebox.back.token.MailTokenService;
import com.safebox.back.user.entity.User;
import com.safebox.back.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RpiServiceImpl implements RpiService {
    private final TotpService totpService;
    private final MailService mailService;
    private final RpiRepository rpiRepository;
    private final MailTokenService mailTokenService;
    private final RpiAccessStatus rpiAccessStatus;

    private static final String BASE_URL = "http://safebox-rssh:5050";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ResponseDto<String> addUser(AddRpiDto reqDto) {
        String uuid = UUID.randomUUID().toString();
        String username = "u_" + uuid.substring(0, 8);
        reqDto.setRpiUser(username);

        String url = BASE_URL + "/add_user";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("X-TOTP", totpService.getTotp());

        Map<String, Object> body = Map.of(
                "rpiUser", username,
                "port", reqDto.getPort(),
                "pubkey", reqDto.getPubkey()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                Rpi rpi = Rpi.builder().rpiId(uuid).port(reqDto.getPort()).user(User.builder().id(reqDto.getUser()).build()).build();
                rpiRepository.save(rpi);
                return ResponseDto.success("라즈베리 파이 추가 성공", uuid);
            }
            return ResponseDto.fail("라즈베리 파이 추가 실패", null);
        } catch (RestClientException e) {
            return ResponseDto.fail("라즈베리 파이 추가 실패", null);
        }
    }

    @Override
    public ResponseDto<Void> deleteUser(String rpiUser) {
        String sshUser = "u_" + rpiUser.substring(0, 8);
        String url = BASE_URL + "/delete_user";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.set("X-TOTP", totpService.getTotp());

        Map<String, Object> body = Map.of("rpiUser", sshUser);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                rpiRepository.deleteById(rpiUser);
                return ResponseDto.success("라즈베리파이 삭제 성공");
            }
            return ResponseDto.fail("라즈베리파이 삭제 실패");
        } catch (RestClientException e) {
            return ResponseDto.fail("라즈베리파이 삭제 실패");
        }
    }

    @Override
    public ResponseDto<Void> arrived(RpiParcelUuidDto requestDto) {
        Optional<Rpi> rpi = rpiRepository.findById(requestDto.getRpiUuid());
        if (rpi.isEmpty()) {
            return ResponseDto.fail("택배도착 처리 실패");
        }
        User user = rpi.get().getUser();
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getName());
        rpiAccessStatus.onParcelArrived(rpi.get().getRpiId());
        mailService.sendArrivedMail(user.getEmail(), "택배가 도착했어요!", "ArrivedMail", params);
        return ResponseDto.success("택배도착 처리완료");
    }

    @Override
    public ResponseDto<Void> pickuped(RpiParcelUuidDto requestDto) {
        Optional<Rpi> rpi = rpiRepository.findById(requestDto.getRpiUuid());
        if (rpi.isEmpty()) {
            return ResponseDto.fail("택배회수 처리 실패");
        }
        User user = rpi.get().getUser();
        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getName());
        String token = mailTokenService.generateToken(requestDto.getRpiUuid(), requestDto.getParcelUuid());
        params.put("link", "http://183.101.36.243:41992/api/report/" + token);
        rpiAccessStatus.onParcelRetrieved(rpi.get().getRpiId());
        mailService.sendArrivedMail(user.getEmail(), "택배가 회수되었어요!", "PickupedMail", params);
        return ResponseDto.success("택배회수 처리완료");
    }
}
