package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import net.zzh.dbrest.DbRestPropertisHolder;
import net.zzh.dbrest.annotation.DbCrud;
import net.zzh.dbrest.extend.DefaultResultHandler;
import net.zzh.dbrest.extend.RequestHandler;
import net.zzh.dbrest.extend.ResultHandler;
import net.zzh.dbrest.utils.*;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static net.zzh.dbrest.utils.EntityUtils.entitiesToMapList;
import static net.zzh.dbrest.utils.EntityUtils.entityToMap;
import static net.zzh.dbrest.utils.SqlUtils.*;

@RestController
public class CrudAction {

    private DbCrud dbCrud;
    private String tableName;
    private String keyField;
    private TableDefination tableDefination;
    private RequestHandler requestHandler;
    private ResultHandler resultHandler;

    public CrudAction(DbCrud dbCrud) {
        this.tableName = dbCrud.tableName();
        this.keyField = dbCrud.keyField();
        this.dbCrud = dbCrud;
        try {
             this.tableDefination = DbTableManage.getTableDefination(this.tableName);
        } catch (Exception e) {
            StaticLog.error(e);
        }
        this.createRequestHandler();
        this.createResultHandler();
    }

    public Object save(HttpServletRequest httpServletRequest) {
        try {
            String contentType = httpServletRequest.getHeader("Content-Type");
            Entity entity = new Entity(tableName);
            //判断是否是application/json请求
            if (StrUtil.isNotEmpty(contentType) && contentType.contains("json")) {
                JSONObject params = getJsonParams(httpServletRequest);
                Map<String, Object> parameterMap = invokeRequestHandler(params, ReflectUtil.getMethodByName(this.getClass(), "save"));
                parameterMap.forEach((key, value) -> {
                    if (tableDefination != null && tableDefination.getJdbcType(key).isPresent()) {
                        entity.set(key, TypeResolver.toObject(tableDefination.getJdbcType(key).get(), value == null ? "" : String.valueOf(value)));
                    } else {
                        entity.set(key, value == null ? "" : String.valueOf(value));
                    }
                });
            }else {
                Map parameterMap = getRequestMap(httpServletRequest);
                parameterMap = invokeRequestHandler((Map) parameterMap, ReflectUtil.getMethodByName(this.getClass(), "save"));
                parameterMap.forEach((key, value) -> {
                    if (tableDefination != null && tableDefination.getJdbcType((String) key).isPresent()) {
                        entity.set((String) key, TypeResolver.toObject(tableDefination.getJdbcType((String)key).get(), value == null ? "" : String.valueOf(value)));
                    } else {
                        entity.set((String) key, value == null ? "" : String.valueOf(value));
                    }
                });
            }
            if (CollectionUtil.isEmpty(entity)) {
                throw new RuntimeException("参数不能为空");
            }
            Map<String,Object> result = new HashMap();
            result.put("type", !StrUtil.isEmptyIfStr(entity.get(keyField)) ? "update" : "insert");
            if (!StrUtil.isEmptyIfStr(entity.get(keyField))) {
                int update = DbManage.getDb().update(entity, new Entity().set(keyField, entity.getStr(keyField)));
                result.put("effects", update);
                result.put(keyField, entity.getStr(keyField));
            } else {
                switch (dbCrud.idtype()) {
                    case  AUTO:
                        Long aLong = DbManage.getDb().insertForGeneratedKey(entity);
                        result.put(keyField, aLong);
                        break;
                    case UUID:
                        String id1 = IdUtil.randomUUID();
                        entity.set(keyField, id1);
                        DbManage.getDb().insert(entity);
                        result.put(keyField, id1);
                        break;
                    case SIMPLE_UUID:
                        String id2 = IdUtil.simpleUUID();
                        entity.set(keyField, id2);
                        DbManage.getDb().insert(entity);
                        result.put(keyField, id2);
                        break;
                    default:
                        DbManage.getDb().insert(entity);
                        break;
                }
            }
            return invokeResultHandler(result,ReflectUtil.getMethodByName(this.getClass(),"save"));
        } catch (Exception e) {
            StaticLog.error(e,"save请求失败");
            return invokeResultHandler(e,ReflectUtil.getMethodByName(this.getClass(),"save"));
        }
    }

    public Map<String, Object> invokeRequestHandler(Map<String,Object> params, Method method) {
        if (this.requestHandler != null) {
           return this.requestHandler.handler(params, this.dbCrud, method);
        }
        return params;
    }

    public Object invokeResultHandler(Object obj,Method method) {
        if (this.resultHandler != null) {
           return this.resultHandler.handler(obj, this.dbCrud, method);
        }
        return obj;
    }

    private JSONObject getJsonParams(HttpServletRequest httpServletRequest) {
        try {
            String read = IoUtil.read(httpServletRequest.getInputStream(), "utf-8");
            return JSONUtil.parseObj(read);
        } catch (IOException e) {
            StaticLog.error(e,"获取json参数异常");
        }
        return new JSONObject();
    }

    public Object delete(HttpServletRequest httpServletRequest) {
        Map<String, String> requestMap = getRequestMap(httpServletRequest);
        requestMap = invokeRequestHandler((Map) requestMap, ReflectUtil.getMethodByName(this.getClass(), "delete"));
        String id = requestMap.get("id");
        try {
            int del = DbManage.getDb().del(tableName, keyField, id);
            return invokeResultHandler(MapUtil.of("effects", del), ReflectUtil.getMethodByName(this.getClass(), "delete"));
        } catch (Exception e) {
            StaticLog.error(e,"delete请求异常");
            return invokeResultHandler(e, ReflectUtil.getMethodByName(this.getClass(), "delete"));
        }
    }

    public Object getById(HttpServletRequest httpServletRequest) {
        Map<String, String> requestMap = getRequestMap(httpServletRequest);
        requestMap = invokeRequestHandler((Map) requestMap, ReflectUtil.getMethodByName(this.getClass(), "getById"));
        String id = requestMap.get("id");
        List<Entity> entities = null;
        try {
            entities = DbManage.getDb().findBy(tableName, keyField, id);
            if (CollectionUtil.isNotEmpty(entities)) {
                Entity entity = entities.get(0);
                Map result = entityToMap(entity, false);
                return invokeResultHandler(result, ReflectUtil.getMethodByName(this.getClass(), "getById"));
            }
            return invokeResultHandler(new HashMap<>(), ReflectUtil.getMethodByName(this.getClass(), "getById"));
        } catch (Exception e) {
            StaticLog.error(e,"getById请求异常");
            return invokeResultHandler(e, ReflectUtil.getMethodByName(this.getClass(), "getById"));
        }
    }

    public Object findList(HttpServletRequest httpServletRequest) {
        try {
            Map<String, String> requestMap = getRequestMap(httpServletRequest);
            requestMap = invokeRequestHandler((Map) requestMap, ReflectUtil.getMethodByName(this.getClass(), "findList"));
            String orderBy = requestMap.get("orderBy");
            requestMap.remove("orderBy");
            Entity entity = Entity.create(tableName);
            if (CollectionUtil.isNotEmpty(requestMap)) {
                //bet类型转成lte,gte
                resovelBet(requestMap);
                requestMap.forEach((k,v) -> {
                    Optional<String[]> conditionChar = getConditionChar(k, "_");
                    if (tableDefination != null && tableDefination.getJdbcType(getSplitKey(k,"_")).isPresent()) {
                        entity.set(conditionChar.get()[0], TypeResolver.toObject(tableDefination.getJdbcType(getSplitKey(k,"_")).get(), v == null ? "" : String.valueOf(v)));
                    } else {
                        entity.set(conditionChar.get()[0], wapperParams(conditionChar.get()[1], v));
                    }
                });
            }
            List<Map> maps = entitiesToMapList(DbManage.findAll(entity));
            //排序，findList只支持单字段排序
            if (StrUtil.isNotEmpty(orderBy)) {
                String[] split = orderBy.split(" ");
                String filedName = split[0].trim();
                //如果长度==1，默认降序
                boolean asc = split.length != 1 && ("asc".equals(split[1].trim()));
                CollectionUtil.sort(maps, Comparator.comparing((o1)->{
                    if (o1 == null) { return ""; }
                    Object name1 = o1.get(filedName);
                    if (name1 == null) { return ""; }
                    return String.valueOf(name1);
                }));
                if (!asc) {
                    maps = CollectionUtil.reverse(maps);
                }
            }
            return invokeResultHandler(maps, ReflectUtil.getMethodByName(this.getClass(), "findList"));
        } catch (Exception e) {
            StaticLog.error(e,"findList请求异常");
            return invokeResultHandler(e, ReflectUtil.getMethodByName(this.getClass(), "findList"));
        }
    }

    public Map<String,String> resovelBet(Map<String,String> entity) {
        if (CollectionUtil.isEmpty(entity)) {
            return entity;
        }
        List<String> betKeys = new ArrayList<>();
        entity.keySet().forEach(k -> {
            if ("between".equals(getConditionChar(k, "_").get()[1])) {
                betKeys.add(k);
            }
        });
        if (!CollectionUtil.isEmpty(betKeys)) {
            for (String betKey : betKeys) {
                String betValues = (String)entity.remove(betKey);
                String[] split = betValues.split(",");
                Assert.isTrue(split.length == 2, "between类型参数[" + betKey + "]的值必须以逗号分割的两个字符");
                entity.put(getSplitKey(betKey, "_") + "_gte", split[0]);
                entity.put(getSplitKey(betKey, "_") + "_lte", split[1]);
            }
        }
        return entity;
    }

    public Object findPage(HttpServletRequest httpServletRequest) {
        try {
            Map<String, String> requestMap = getRequestMap(httpServletRequest);
            requestMap = invokeRequestHandler((Map) requestMap, ReflectUtil.getMethodByName(this.getClass(), "findPage"));
            String pageNum = StrUtil.emptyToDefault(requestMap.get("pageNum"), "1");
            String pageSize = StrUtil.emptyToDefault(requestMap.get("pageSize"),"10");
            Page page1 = new Page(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize));
            requestMap.remove("pageNum");
            requestMap.remove("pageSize");
            Entity entity = Entity.create(tableName);
            if (CollectionUtil.isNotEmpty(requestMap)) {
                //bet类型转成lte,gte
                resovelBet(requestMap);
                requestMap.forEach((k,v) -> {
                    if (!"orderBy".equals(k)) {
                        Optional<String[]> conditionChar = getConditionChar(k, "_");
                        if (tableDefination != null && tableDefination.getJdbcType(getSplitKey(k,"_")).isPresent()) {
                            entity.set(conditionChar.get()[0], TypeResolver.toObject(tableDefination.getJdbcType(getSplitKey(k,"_")).get(), v == null ? "" : String.valueOf(v)));
                        } else {
                            entity.set(conditionChar.get()[0], wapperParams(conditionChar.get()[1], v));
                        }
                    }else {
                        if (StrUtil.isNotEmpty(v)) {
                            String[] items = v.trim().split(",");
                            for (String item : items) {
                                String[] keyDirect = item.split(" ");
                                //长度等于1，只传字段名，默认降序
                                if (keyDirect.length == 1) {
                                    page1.addOrder(new Order(item.trim(), Direction.DESC));
                                }else {
                                    page1.addOrder(new Order(keyDirect[0].trim(), keyDirect[1].trim().equals("asc") ? Direction.ASC : Direction.DESC));
                                }
                            }
                            page1.addOrder();
                        }
                    }
                });
            }
            PageResult<Entity> page = DbManage.page(entity, page1);
            net.zzh.dbrest.page.PageResult<Map> pageResult = new net.zzh.dbrest.page.PageResult<>(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
            pageResult.setTotal(page.getTotal());
            pageResult.setDatas(entitiesToMapList(page));
            return invokeResultHandler(pageResult, ReflectUtil.getMethodByName(this.getClass(), "findPage"));
        } catch (Exception e) {
            StaticLog.error(e,"findPage请求异常");
            return invokeResultHandler(e, ReflectUtil.getMethodByName(this.getClass(), "findPage"));
        }
    }

    private void createResultHandler() {
        try {
            Class<? extends ResultHandler> resultHandlerClass = this.dbCrud.resultHandler();
            ResultHandler globalResultHandler = DbRestPropertisHolder.getGlobalResultHandler();
            if (resultHandlerClass == DefaultResultHandler.class && globalResultHandler != null) {
                this.resultHandler = globalResultHandler;
            } else {
                this.resultHandler = resultHandlerClass.newInstance();
            }
        } catch (Exception e) {
            StaticLog.error(e,"初始化ResultHandler失败");
        }
    }

    private void createRequestHandler() {
        try {
            Class<? extends RequestHandler> requestHandler = this.dbCrud.requestHandler();
            if (!requestHandler.isInterface()) {
                this.requestHandler = requestHandler.newInstance();
            }else {
                this.requestHandler = DbRestPropertisHolder.getGlobalRequestHandler();
            }
        } catch (Exception e) {
            StaticLog.error(e,"初始化RequestHandler失败");
        }
    }

    public Map<String, String> getRequestMap(HttpServletRequest httpServletRequest) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, String> resultMap = new HashMap<>();
        parameterMap.forEach((k,v) -> {
            if (v != null && v.length > 0 && StrUtil.isNotEmpty(v[0])) {
                resultMap.put(k, ArrayUtil.join(v, ","));
            }
        });
        return resultMap;
    }

}
