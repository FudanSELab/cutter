package cn.icedsoul.cutter.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * @author IcedSoul
 * @date 19-6-17 下午3:42
 */
@Log
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoWayRelation {
    private Double aToB;
    private Double bToA;
}
