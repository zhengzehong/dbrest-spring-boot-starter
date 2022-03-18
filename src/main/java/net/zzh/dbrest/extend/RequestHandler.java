package net.zzh.dbrest.extend;

import java.util.Map;

public interface RequestHandler {

    Map<String,Object> handler(Map<String, Object> requestParams);

}
