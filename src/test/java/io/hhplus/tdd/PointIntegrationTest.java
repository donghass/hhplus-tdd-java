package io.hhplus.tdd;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    // PointService는 컨트롤러의 의존성으로 주입되며, 여기서 목(mock)으로 대체합니다.
    @MockBean
    private PointService pointService;

    @DisplayName("포인트 조회")
    @Test
    void pointSelect() throws Exception {

        // Arrange
        long userId = 1L;
        // 예상 UserPoint: userId가 1이고, 포인트가 1000L, updateMillis는 현재 시간 (테스트에서는 equals 비교 시 중요하지 않음)
        UserPoint expectedUserPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        when(pointService.readUserPoint(eq(userId))).thenReturn(expectedUserPoint);

        // Act 조회 호출 GET 요청을 보내 컨트롤러가 올바른 값을 반환하는지 확인
        String url = "http://localhost:" + port + "/point/" + userId;
        ResponseEntity<UserPoint> response = restTemplate.getForEntity(url, UserPoint.class);

        // Assert
        // HTTP 상태 코드가 200 OK이고, 반환된 UserPoint가 예상과 일치하는지 검증
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserPoint, response.getBody());
    }

    @DisplayName("포인트 충전")
    @Test
    void pointCharge() throws Exception {
        // Arrange
        long userId = 1L;
        long amount = 500L;
//        long updatedPoint = 50000L;
        // 예상 UserPoint: userId가 1이고, 포인트가 1000L, updateMillis는 현재 시간 (테스트에서는 equals 비교 시 중요하지 않음)
        UserPoint userPoint = new UserPoint(userId, 1500L, System.currentTimeMillis());
        when(pointService.readUserPoint(eq(userId))).thenReturn(userPoint);

        //조회 호출 patch 요청을 보내 컨트롤러가 올바른 값을 반환하는지 확인
        String url = "http://localhost:" + port + "/point/" + userId + "/charge";
        // Act
        // PATCH 요청은 요청 본문에 충전할 금액(amount)를 보내고, 응답으로 UserPoint를 반환합니다.
        UserPoint response = restTemplate.patchForObject(url, amount, UserPoint.class);

        // Assert
        // 응답 HTTP 상태코드는 TestRestTemplate의 patchForObject가 성공 시 반환한 객체가 null이 아니면 성공한 것으로 판단하고,
        // 반환된 UserPoint 객체가 예상 값과 일치하는지 비교합니다.
        assertEquals(userPoint, response, "포인트 충전 후 UserPoint 객체가 예상과 일치해야 합니다.");

    }

    @DisplayName("포인트 시용")
    @Test
    void pointUse() throws Exception {
        // Arrange
        long userId = 1L;
        long amount = 500L;
//        long updatedPoint = 50000L;
        // 예상 UserPoint: userId가 1이고, 포인트가 1000L, updateMillis는 현재 시간 (테스트에서는 equals 비교 시 중요하지 않음)
        UserPoint userPoint = new UserPoint(userId, 1500L, System.currentTimeMillis());
        when(pointService.readUserPoint(eq(userId))).thenReturn(userPoint);

        //사용 호출 -- patch 요청을 보내 컨트롤러가 올바른 값을 반환하는지 확인
        String url = "http://localhost:" + port + "/point/" + userId + "/use";
        // Act
        // PATCH 요청은 요청 본문에 충전할 금액(amount)를 보내고, 응답으로 UserPoint를 반환합니다.
        UserPoint response = restTemplate.patchForObject(url, amount, UserPoint.class);

        // Assert
        // 응답 HTTP 상태코드는 TestRestTemplate의 patchForObject가 성공 시 반환한 객체가 null이 아니면 성공한 것으로 판단하고,
        // 반환된 UserPoint 객체가 예상 값과 일치하는지 비교합니다.
        assertEquals(userPoint, response, "포인트 충전 후 UserPoint 객체가 예상과 일치해야 합니다.");

    }
}
