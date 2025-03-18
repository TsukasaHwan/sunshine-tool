package org.sunshine.core.cache;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/03/30
 */
public record RedisClientImpl(RedisTemplate<String, Object> redisTemplate) implements RedisClient {

    @Override
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.expire(key, time, timeUnit);
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(Arrays.asList(keys));
            }
        }
    }

    @Override
    public Optional<Object> getValue(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValueWithExpire(String key, Object value, long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } else {
            setValue(key, value);
        }
    }

    @Override
    public void setValueWithExpire(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long increment(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于等于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Double increment(String key, double delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于等于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decrement(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于等于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    @Override
    public Double decrement(String key, double delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于等于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public List<Object> getMultipleValues(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Optional<Object> hashGet(String key, String item) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, item));
    }

    @Override
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void hashPutAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hashPutAllWithExpire(String key, Map<String, Object> map, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
    }

    @Override
    public void hashPut(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    @Override
    public void hashPutWithExpire(String key, String item, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, item, value);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
    }

    @Override
    public Long hashDelete(String key, Object... items) {
        return redisTemplate.opsForHash().delete(key, items);
    }

    @Override
    public Boolean hashHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    @Override
    public Long hashIncrement(String key, String item, long by) {
        if (by < 0) {
            throw new IllegalArgumentException("递增因子必须大于等于0");
        }
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    @Override
    public Long hashDecrement(String key, String item, long by) {
        if (by < 0) {
            throw new IllegalArgumentException("递减因子必须大于等于0");
        }
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    @Override
    public Double hashIncrement(String key, String item, double by) {
        if (by < 0) {
            throw new IllegalArgumentException("递增因子必须大于等于0");
        }
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    @Override
    public Double hashDecrement(String key, String item, double by) {
        if (by < 0) {
            throw new IllegalArgumentException("递减因子必须大于等于0");
        }
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    @Override
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Boolean setIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long setAddMembers(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long setAddMembersWithExpire(String key, long time, TimeUnit timeUnit, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    @Override
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long setRemoveMembers(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Optional<Object> listGetIndex(String key, long index) {
        return Optional.ofNullable(redisTemplate.opsForList().index(key, index));
    }

    @Override
    public Long listRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long listRightPushWithExpire(String key, Object value, long time, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    @Override
    public Long listRightPushAll(String key, List<Object> values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long listRightPushAllWithExpire(String key, List<Object> values, long time, TimeUnit timeUnit) {
        Long count = redisTemplate.opsForList().rightPushAll(key, values);
        if (time > 0) {
            expire(key, time, timeUnit);
        }
        return count;
    }

    @Override
    public void listUpdateIndex(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    @Override
    public Long listRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public Boolean zSetAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Double zSetIncrementScore(String key, Object value, double score) {
        if (score < 0) {
            throw new IllegalArgumentException("增加分数必须大于等于0");
        }
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    @Override
    public Double zSetDecrementScore(String key, Object value, double score) {
        if (score < 0) {
            throw new IllegalArgumentException("减少分数必须大于等于0");
        }
        return redisTemplate.opsForZSet().incrementScore(key, value, -score);
    }

    @Override
    public Long zSetAddAll(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    @Override
    public Long zSetReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    @Override
    public Double zSetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zSetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    @Override
    public List<String> scanKeys(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        }
        return keys;
    }

    @Override
    public void batchDelete(String pattern) {
        List<String> keys = scanKeys(pattern);
        redisTemplate.delete(keys);
    }

}
