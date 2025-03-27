package io.hhplus.tdd.point;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    public final PointService pointService;
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }
    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(@PathVariable long id) {
        //PointService pointService = new PointService();
        //pointService.readUserPoint(id);
        return pointService.readUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(@PathVariable long id) {
        //PointService ps = new PointService();
        return pointService.readUserPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(@PathVariable long id,@RequestBody long amount) {
        //PointService ps = new PointService();
        pointService.updateUserChargePoint(id,amount);
        pointService.createUserPointHistory(id,amount,CHARGE);
        return pointService.readUserPoint(id);
    }


    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(@PathVariable long id,@RequestBody long amount) {
        //PointService ps = new PointService();
        pointService.updateUserUsePoint(id,amount);
        pointService.createUserPointHistory(id,amount,USE);
        return pointService.readUserPoint(id);
    }
}
