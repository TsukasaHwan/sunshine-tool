package org.sunshine.core.cache;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/03/30
 */
public class RedisClientImpl implements RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisClientImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    @Override
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, timeUnit);
        } else {
            set(key, value);
        }
    }

    @Override
    public Long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    @Override
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public Optional<Object> hget(String key, String item) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, item));
    }

    @Override
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hmset(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            expire(key, time);
        }
    }

    @Override
    public void hset(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    @Override
    public void hset(String key, String item, Object value, long time) {
        redisTemplate.opsForHash().put(key, item, value);
        if (time > 0) {
            expire(key, time);
        }
    }

    @Override
    public Long hdel(String key, Object... item) {
        return redisTemplate.opsForHash().delete(key, item);
    }

    @Override
    public Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    @Override
    public Double hincr(String key, String item, double by) {
        if (by < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    @Override
    public Double hdecr(String key, String item, double by) {
        if (by < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    @Override
    public Set<Object> sGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long sSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Long sSet(String key, long time, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    @Override
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Long sDel(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    @Override
    public List<Object> lGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    @Override
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public Optional<Object> lIndex(String key, long index) {
        return Optional.ofNullable(redisTemplate.opsForList().index(key, index));
    }

    @Override
    public Long lSet(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long lSet(String key, Object value, long time) {
        Long count = redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    @Override
    public Long lSet(String key, List<Object> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    @Override
    public Long lSet(String key, List<Object> value, long time) {
        Long count = redisTemplate.opsForList().rightPushAll(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return count;
    }

    @Override
    public void lUpdateIndex(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    @Override
    public Long lDel(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public Boolean zsSet(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Double zsIncrScore(String key, Object value, double score) {
        if (score < 0) {
            throw new RuntimeException("增加分数必须大于0");
        }
        return redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    @Override
    public Double zsDecrScore(String key, Object value, double score) {
        if (score < 0) {
            throw new RuntimeException("减少分数必须大于0");
        }
        return redisTemplate.opsForZSet().incrementScore(key, value, -score);
    }

    @Override
    public Long zsBatchSet(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    @Override
    public Long zsReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    @Override
    public Double zsScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zsRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> zsReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    @Override
    public List<String> scan(String pattern) {
        if (pattern == null || "".equals(pattern)) {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>();
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .build();
        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        }
        return keys;
    }

    @Override
    public void batchDel(String pattern) {
        List<String> keys = scan(pattern);
        redisTemplate.delete(keys);
    }

    @Override
    public StreamInfo.XInfoGroups streamGroups(String stream) {
        return redisTemplate.opsForStream().groups(stream);
    }

    @Override
    public boolean streamCreateGroup(String stream, String group) {
        String result = redisTemplate.opsForStream().createGroup(stream, group);
        return "OK".equals(result);
    }

    @Override
    public StreamInfo.XInfoConsumers streamConsumers(String stream, String group) {
        return redisTemplate.opsForStream().consumers(stream, group);
    }

    @Override
    public RecordId streamAdd(StringRecord record) {
        return redisTemplate.opsForStream().add(record);
    }

    @Override
    public RecordId streamAdd(Record<String, ?> record) {
        return redisTemplate.opsForStream().add(record);
    }

    @Override
    public RecordId streamAdd(String stream, Map<String, ?> value) {
        return redisTemplate.opsForStream().add(stream, value);
    }

    @Override
    public <T> List<ObjectRecord<String, T>> streamRangeAll(Class<T> clazz, String stream) {
        return streamRange(clazz, stream, Range.unbounded());
    }

    @Override
    public <T> List<ObjectRecord<String, T>> streamRange(Class<T> clazz, String stream, Range<String> range) {
        return redisTemplate.opsForStream().range(clazz, stream, range);
    }

    @Override
    public Optional<PendingMessagesSummary> streamPending(String stream, String group) {
        return Optional.ofNullable(redisTemplate.opsForStream().pending(stream, group));
    }

    @Override
    public PendingMessages streamPending(String stream, Consumer consumer) {
        return redisTemplate.opsForStream().pending(stream, consumer);
    }

    @Override
    public PendingMessages streamPending(String stream, Consumer consumer, Range<?> range, long count) {
        return redisTemplate.opsForStream().pending(stream, consumer, range, count);
    }

    @Override
    public List<MapRecord<String, Object, Object>> streamClaim(String stream, String consumerGroup, String newOwner, RedisStreamCommands.XClaimOptions xClaimOptions) {
        return redisTemplate.opsForStream().claim(stream, consumerGroup, newOwner, xClaimOptions);
    }

    @Override
    public Long streamAck(String group, Record<String, ?> record) {
        return redisTemplate.opsForStream().acknowledge(group, record);
    }

    @Override
    public Long streamAck(String stream, String group, String... recordIds) {
        return redisTemplate.opsForStream().acknowledge(stream, group, recordIds);
    }

    @Override
    public Long streamAck(String stream, String group, RecordId... recordIds) {
        return redisTemplate.opsForStream().acknowledge(stream, group, recordIds);
    }

    @Override
    public Long streamTrim(String stream, long limit) {
        if (limit < 0) {
            throw new RuntimeException("流保留数必须大于或等于0");
        }
        return redisTemplate.opsForStream().trim(stream, limit);
    }

    @Override
    public RedisTemplate<String, Object> redisTemplate() {
        return this.redisTemplate;
    }
}
