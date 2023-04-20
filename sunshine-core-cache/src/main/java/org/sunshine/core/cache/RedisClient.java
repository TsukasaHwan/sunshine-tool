package org.sunshine.core.cache;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/03/30
 */
public interface RedisClient {

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 是否成功
     */
    Boolean expire(String key, long time);

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    Long getExpire(String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    Boolean hasKey(String key);

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    void del(String... key);

    // ============================String=============================//

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    Optional<Object> get(String key);

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     */
    void set(String key, Object value, long time);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间 time要大于0 如果time小于等于0 将设置无限期
     * @param timeUnit 时间单位
     */
    void set(String key, Object value, long time, TimeUnit timeUnit);

    /**
     * 递增
     *
     * @param key
     * @param delta 要增加几(大于0)
     * @return
     */
    Long incr(String key, long delta);

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    Long decr(String key, long delta);

    // ================================Map=================================//

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    Optional<Object> hget(String key, String item);

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    Map<Object, Object> hmget(String key);

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    void hmset(String key, Map<String, Object> map);

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     */
    void hmset(String key, Map<String, Object> map, long time);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     */
    void hset(String key, String item, Object value);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     */
    void hset(String key, String item, Object value, long time);

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     * @return
     */
    Long hdel(String key, Object... item);

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    Boolean hHasKey(String key, String item);

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    Double hincr(String key, String item, double by);

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    Double hdecr(String key, String item, double by);

    // ============================set=============================//

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    Set<Object> sGet(String key);

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    Boolean sHasKey(String key, Object value);

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    Long sSet(String key, Object... values);

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值可以是多个
     * @return 成功个数
     */
    Long sSet(String key, long time, Object... values);

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    Long sSize(String key);

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值可以是多个
     * @return 移除的个数
     */
    Long sDel(String key, Object... values);

    // ===============================list=================================//

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    List<Object> lGet(String key, long start, long end);

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    Long lSize(String key);

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    Optional<Object> lIndex(String key, long index);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    Long lSet(String key, Object value);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    Long lSet(String key, Object value, long time);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    Long lSet(String key, List<Object> value);

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    Long lSet(String key, List<Object> value, long time);

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    void lUpdateIndex(String key, long index, Object value);

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    Long lDel(String key, long count, Object value);

    /**
     * zSet添加或更新
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return
     */
    Boolean zsSet(String key, Object value, double score);

    /**
     * 使用加法操作分数
     *
     * @param key   键
     * @param value 值
     * @param score 要加的分数
     * @return
     */
    Double zsIncrScore(String key, Object value, double score);

    /**
     * 使用减法操作分数
     *
     * @param key   键
     * @param value 值
     * @param score 要减的分数
     * @return
     */
    Double zsDecrScore(String key, Object value, double score);

    /**
     * 批量更新或者添加到zSet
     *
     * @param key    键
     * @param tuples 要更新的分数
     * @return
     */
    Long zsBatchSet(String key, Set<ZSetOperations.TypedTuple<Object>> tuples);

    /**
     * 获取倒序排名
     *
     * @param key   键
     * @param value 值
     * @return
     */
    Long zsReverseRank(String key, Object value);

    /**
     * 获取分数
     *
     * @param key   键
     * @param value 值
     * @return
     */
    Double zsScore(String key, Object value);

    /**
     * 从分数低到高正序取出数据
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zsRangeWithScores(String key, long start, long end);

    /**
     * 从分数高到低倒序取出数据
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return
     */
    Set<ZSetOperations.TypedTuple<Object>> zsReverseRangeWithScores(String key, long start, long end);

    /**
     * 根据前缀批量获取key
     *
     * @param prefix
     * @return
     */
    Set<String> batchGetKeys(String prefix);

    /**
     * 根据前缀批量删除key
     *
     * @param prefix 前缀
     */
    void batchDel(String prefix);

    /**
     * 获取Stream组信息
     *
     * @param stream Stream key
     * @return XInfoGroups
     */
    StreamInfo.XInfoGroups streamGroups(String stream);

    /**
     * 创建消费组
     *
     * @param stream Stream key
     * @param group  组
     * @return OK
     */
    boolean streamCreateGroup(String stream, String group);

    /**
     * 添加Stream消息
     *
     * @param record StringRecord
     * @return RecordId
     */
    RecordId streamAdd(StringRecord record);

    /**
     * 添加Stream消息
     *
     * @param record record
     * @return RecordId
     */
    RecordId streamAdd(Record<String, ?> record);

    /**
     * 添加Stream消息
     *
     * @param stream Stream key
     * @param value  Map消息
     * @return RecordId
     */
    RecordId streamAdd(String stream, Map<String, ?> value);

    /**
     * 从指定流中读取所有记录作为ObjectRecord
     *
     * @param clazz  目标类型
     * @param stream Stream key
     * @param <T>    T
     * @return 消息
     */
    <T> List<ObjectRecord<String, T>> streamRangeAll(Class<T> clazz, String stream);

    /**
     * 从特定范围内的流中读取所有记录作为ObjectRecord
     *
     * @param clazz  目标类型
     * @param stream Stream key
     * @param range  范围
     * @param <T>    T
     * @return 消息
     */
    <T> List<ObjectRecord<String, T>> streamRange(Class<T> clazz, String stream, Range<String> range);

    /**
     * Stream确认消息
     *
     * @param group  组
     * @param record 消息
     * @return Long
     */
    Long streamAck(String group, Record<String, ?> record);

    /**
     * Stream确认消息
     *
     * @param stream    Stream key
     * @param group     组
     * @param recordIds 消息id
     * @return Long
     */
    Long streamAck(String stream, String group, String... recordIds);
}
