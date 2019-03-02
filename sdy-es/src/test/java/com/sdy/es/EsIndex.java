package com.sdy.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019/3/2.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsIndex {

    private String did;

    private String content;

    private String did2;

    private String did3;


    public static String createConent(int size) {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, 1024).forEach(i -> {
            sb.append("a");
        });
        return sb.toString();
    }


    public static EsIndex create(String content) {
        return new EsIndex(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyyMMddHHmmssSSS")), content, UuidUtil.getTimeBasedUuid().toString(), UuidUtil.getTimeBasedUuid().toString());
    }



}
