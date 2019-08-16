package cn.icedsoul.cutter.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author IcedSoul
 * @date 19-8-16 上午10:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int status;
    private String message;
    private Object content;
}
