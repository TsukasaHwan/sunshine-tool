package org.sunshine.core.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/03/30
 */
public interface RedisClient {
    // ================ Key Value ================

    Boolean expire(String key, long time, TimeUnit timeUnit);

    Long getExpire(String key, TimeUnit timeUnit);

    Boolean hasKey(String key);

    void delete(String... keys);

    Optional<Object> getValue(String key);

    void setValue(String key, Object value);

    void setValueWithExpire(String key, Object value, long time, TimeUnit timeUnit);

    void setValueWithExpire(String key, Object value, Duration duration);

    Long increment(String key);

    Long increment(String key, long delta);

    Double increment(String key, double delta);

    Long decrement(String key, long delta);

    Double decrement(String key, double delta);

    List<Object> getMultipleValues(Collection<String> keys);

    // ================ Hash ================

    Optional<Object> hashGet(String key, String item);

    Map<Object, Object> hashGetAll(String key);

    void hashPutAll(String key, Map<String, Object> map);

    void hashPutAllWithExpire(String key, Map<String, Object> map, long time, TimeUnit timeUnit);

    void hashPut(String key, String item, Object value);

    void hashPutWithExpire(String key, String item, Object value, long time, TimeUnit timeUnit);

    Long hashDelete(String key, Object... items);

    Boolean hashHasKey(String key, String item);

    Long hashIncrement(String key, String item, long by);

    Long hashDecrement(String key, String item, long by);

    Double hashIncrement(String key, String item, double by);

    Double hashDecrement(String key, String item, double by);

    // ================ Set ================

    Set<Object> setMembers(String key);

    Boolean setIsMember(String key, Object value);

    Long setAddMembers(String key, Object... values);

    Long setAddMembersWithExpire(String key, long time, TimeUnit timeUnit, Object... values);

    Long setSize(String key);

    Long setRemoveMembers(String key, Object... values);

    // ================ List ================

    List<Object> listRange(String key, long start, long end);

    Long listGetSize(String key);

    Optional<Object> listGetIndex(String key, long index);

    Long listRightPush(String key, Object value);

    Long listRightPushWithExpire(String key, Object value, long time, TimeUnit timeUnit);

    Long listRightPushAll(String key, List<Object> values);

    Long listRightPushAllWithExpire(String key, List<Object> values, long time, TimeUnit timeUnit);

    void listUpdateIndex(String key, long index, Object value);

    Long listRemove(String key, long count, Object value);

    // ================ ZSet ================

    Boolean zSetAdd(String key, Object value, double score);

    Double zSetIncrementScore(String key, Object value, double score);

    Double zSetDecrementScore(String key, Object value, double score);

    Long zSetAddAll(String key, Set<ZSetOperations.TypedTuple<Object>> tuples);

    Long zSetReverseRank(String key, Object value);

    Double zSetScore(String key, Object value);

    Set<ZSetOperations.TypedTuple<Object>> zSetRangeWithScores(String key, long start, long end);

    Set<ZSetOperations.TypedTuple<Object>> zSetReverseRangeWithScores(String key, long start, long end);

    // ================ 其他 ================

    List<String> scanKeys(String pattern);

    void batchDelete(String pattern);

    RedisTemplate<String, Object> redisTemplate();
}
