package com.safebox.back.rpi.service;

import com.safebox.back.rpi.dto.AddRpiDto;
import com.safebox.back.rpi.entity.Rpi;
import com.safebox.back.rpi.repository.RpiRepository;
import com.safebox.back.rpi.util.TotpService;
import com.safebox.back.user.entity.User;
import com.safebox.back.util.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RpiServiceImpl implements RpiService {

    private final TotpService totpService;
    private final RpiRepository rpiRepository;

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
}
