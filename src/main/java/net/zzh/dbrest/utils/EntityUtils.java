package net.zzh.dbrest.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.db.Entity;
import cn.hutool.log.StaticLog;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityUtils {

    public static Map entityToMap(Entity entity,boolean remove) {
        if (CollectionUtil.isEmpty(entity)) {
            return entity;
        }
        Map<String, Object> result = new HashMap<>();
        entity.forEach((k,v) -> {
            if (v == null) {
                result.put(k, "");
            }else if (v instanceof String) {
                result.put(k, v);
            }else if(v instanceof Clob){
                if (!remove) {
                    try {
                        Reader characterStream = entity.getClob(k).getCharacterStream();
                        String clobText = IoUtil.read(characterStream);
                        result.put(k, clobText);
                    } catch (Exception e) {
                        StaticLog.error("获取【" + k + "】clob字段错误！");
                    }
                }
            }else if(v instanceof Date){
                result.put(k, DateUtil.formatDateTime((Date) v));
            }
        });
        return result;
    }

    public static List<Map> entitiesToMapList(List<Entity> entities) {
        return entities.stream().map(entity -> entityToMap(entity,true)).collect(Collectors.toList());
    }


}
