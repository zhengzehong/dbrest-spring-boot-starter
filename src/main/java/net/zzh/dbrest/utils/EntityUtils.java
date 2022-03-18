package net.zzh.dbrest.utils;

import cn.hutool.db.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityUtils {

    public static Map entityToMap(Entity entity) {
        if (entity instanceof Entity) {
            return ((Entity) entity).toBean(HashMap.class);
        }
        return entity;
    }

    public static List<Map> entitiesToMapList(List<Entity> entities) {
        return entities.stream().map(entity -> entityToMap(entity)).collect(Collectors.toList());
    }
}
