package com.edusoho.kuozhi.clean.utils.biz;

import com.edusoho.videoplayer.media.M3U8Stream;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理业务工具类
 */
public class BizUtils {

    public static String filterJsonFormat(String url) {
        if (url.contains("?")) {
            String[] urls = url.split("\\?");
            if (urls.length > 1) {
                return urls[0];
            }
        }
        return url;
    }

    private static Pattern M3U8_STREAM_PAT = Pattern.compile(
            "#EXT-X-STREAM-INF:PROGRAM-ID=(\\d+),BANDWIDTH=(\\d+),NAME=\"?(\\w+)\"?", Pattern.DOTALL);

    public static List<M3U8Stream> parseM3U8File(String m3u8Resource) {
        List<M3U8Stream> m3U8Streams = null;
        try {
            m3U8Streams = new ArrayList<>();
            StringReader sr = new StringReader(m3u8Resource);
            BufferedReader reader = new BufferedReader(sr);
            String line;
            M3U8Stream currentItem = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = M3U8_STREAM_PAT.matcher(line);
                if (matcher.find()) {
                    M3U8Stream item = new M3U8Stream();
                    item.setBandwidth(Integer.parseInt(matcher.group(2)));
                    item.setName(matcher.group(3));
                    if ("http".equals(line) || "https".equals(line)) {
                        item.setUrl(line);
                    }
                    currentItem = item;
                    m3U8Streams.add(item);
                }
                if (line.contains("https://") || line.contains("http://")) {
                    currentItem.setUrl(line);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m3U8Streams;
    }

    public static String showTestScore(float originScore) {
        if (originScore == (int) originScore) {
            return (int) Math.floor(originScore) + "";
        } else {
            return (float) (Math.floor(originScore * 10) / 10) + "";
        }
    }
}
