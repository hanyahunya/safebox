package com.safebox.back.rpi.service;

import com.safebox.back.rpi.entity.Rpi;
import com.safebox.back.rpi.repository.RpiRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {
    private final RpiRepository rpiRepository;

    @Override
    public void stream(HttpServletResponse response, String userId) {
        Optional<Rpi> dbRpi = rpiRepository.findByUser_Id(userId);
        if (dbRpi.isEmpty()) {
            return;
        }

        String videoStreamUrl = "http://safebox-rssh:" + dbRpi.get().getPort() + "/video";  // 외부 MJPEG 스트림 URL

        try {
            URI uri = URI.create(videoStreamUrl);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<InputStream> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // Content Type 설정
            response.setContentType("multipart/x-mixed-replace; boundary=frame");

            try (InputStream inputStream = httpResponse.body();
                 OutputStream outputStream = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();  // 실시간으로 전송
                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
