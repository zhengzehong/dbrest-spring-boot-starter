package net.zzh.dbrest.sql;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import net.zzh.dbrest.annotation.DbCrud;
import net.zzh.dbrest.utils.DbTableManage;
import net.zzh.dbrest.utils.SqlUtils;
import net.zzh.dbrest.utils.TableDefination;
import net.zzh.dbrest.utils.TypeResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.zzh.dbrest.utils.EntityUtils.entitiesToMapList;
import static net.zzh.dbrest.utils.EntityUtils.entityToMap;
import static net.zzh.dbrest.utils.SqlUtils.*;

@RestController
public class CrudAction {
    private DbCrud dbCrud;
    private String tableName;
    private String keyField;
    private TableDefination tableDefination;

    public CrudAction(DbCrud dbCrud) {
        this.tableName = dbCrud.tableName();
        this.keyField = dbCrud.keyField();
        this.dbCrud = dbCrud;
        try {
             this.tableDefination = DbTableManage.getTableDefination(this.tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String,Object> save(HttpServletRequest httpServletRequest) throws SQLException {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        if (CollectionUtil.isNotEmpty(parameterMap)) {
            Entity entity = new Entity(tableName);
            parameterMap.forEach((key,value) -> {
                if (tableDefination != null && tableDefination.getJdbcType(key).isPresent()) {
                    entity.set(key, TypeResolver.toObject(tableDefination.getJdbcType(key).get(), value != null ? StrUtil.nullToEmpty(value[0]) : ""));
                }else {
                    entity.set(key, value != null ? StrUtil.nullToEmpty(value[0]) : "");
                }
            });
            Map<String,Object> result = new HashMap();
            result.put("type", !StrUtil.isEmptyIfStr(entity.get(keyField)) ? "update" : "insert");
            if (!StrUtil.isEmptyIfStr(entity.get(keyField))) {
                int update = Db.use().update(entity, new Entity().set(keyField, entity.getStr(keyField)));
                result.put("effects", update);
                result.put(keyField, entity.getStr(keyField));
            } else {
                switch (dbCrud.idtype()) {
                    case  AUTO:
                        Long aLong = Db.use().insertForGeneratedKey(entity);
                        result.put(keyField, aLong);
                        break;
                    case UUID:
                        String id1 = IdUtil.randomUUID();
                        entity.set(keyField, id1);
                        Db.use().insert(entity);
                        result.put(keyField, id1);
                        break;
                    case SIMPLE_UUID:
                        String id2 = IdUtil.simpleUUID();
                        entity.set(keyField, id2);
                        Db.use().insert(entity);
                        result.put(keyField, id2);
                        break;
                    default:
                        Db.use().insert(entity);
                        break;
                }

            }
            return result;
        }
        throw new RuntimeException("参数不能为空");
    }

    public int delete(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        try {
            return Db.use().del(tableName, keyField, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String,Object> getById(HttpServletRequest httpServletRequest) {
        String id = httpServletRequest.getParameter("id");
        List<Entity> entities = null;
        try {
            entities = Db.use().findBy(tableName, keyField, id);
            if (CollectionUtil.isNotEmpty(entities)) {
                Entity entity = entities.get(0);
                return entityToMap(entity, false);
            }
        } catch (SQLException e) {
            StaticLog.error(e);
        }
        return null;
    }

    public List<Map> findList(HttpServletRequest httpServletRequest) {
        try {
            Map<String, String> requestMap = getRequestMap(httpServletRequest);
            Entity entity = Entity.create(tableName);
            if (CollectionUtil.isNotEmpty(requestMap)) {
                requestMap.forEach((k,v) -> {
                    Optional<String[]> conditionChar = getConditionChar(k, "_");
                    if (conditionChar.isPresent()) {
                        entity.set(conditionChar.get()[0], wapperParams(conditionChar.get()[1], v));
                    }else {
                        entity.set(k, v);
                    }
                });
            }
            List<Entity> all = Db.use().findAll(entity);
            return entitiesToMapList(all);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public net.zzh.dbrest.page.PageResult findPage(HttpServletRequest httpServletRequest) {
        try {
            String pageNum = StrUtil.emptyToDefault(httpServletRequest.getParameter("pageNum"), "1");
            String pageSize = StrUtil.emptyToDefault(httpServletRequest.getParameter("pageSize"),"10");
            Page page1 = new Page(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize));
            Map<String, String> requestMap = getRequestMap(httpServletRequest);
            requestMap.remove("pageNum");
            requestMap.remove("pageSize");
            Entity entity = Entity.create(tableName);
            if (CollectionUtil.isNotEmpty(requestMap)) {
                requestMap.forEach((k,v) -> {
                    if (!"orderBy".equals(k)) {
                        Optional<String[]> conditionChar = getConditionChar(k, "_");
                        if (conditionChar.isPresent()) {
                            entity.set(conditionChar.get()[0], wapperParams(conditionChar.get()[1], v));
                        }else {
                            entity.set(k, v);
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
            PageResult<Entity> page = Db.use().page(entity, page1);
            net.zzh.dbrest.page.PageResult<Map> pageResult = new net.zzh.dbrest.page.PageResult<>(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
            pageResult.setTotal(page.getTotal());
            pageResult.setDatas(entitiesToMapList(page));
            return pageResult;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getRequestMap(HttpServletRequest httpServletRequest) {
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, String> resultMap = new HashMap<>();
        parameterMap.forEach((k,v) -> {
            if (v != null && v.length > 0) {
                resultMap.put(k, ArrayUtil.join(v, ","));
            }
        });
        return resultMap;
    }
}
