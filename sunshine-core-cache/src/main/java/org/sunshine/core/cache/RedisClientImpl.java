package org.sunshine.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/03/30
 */
public class RedisClientImpl implements RedisClient {

    private final static Logger log = LoggerFactory.getLogger(RedisClientImpl.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisClientImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
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
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
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
    public Optional<Object> hget(String key, String item) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, item));
    }

    @Override
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    @Override
    public Boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    @Override
    public Double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    @Override
    public Double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    @Override
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Optional<Object> lGetIndex(String key, long index) {
        try {
            return Optional.ofNullable(redisTemplate.opsForList().index(key, index));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public boolean zsSet(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean zsUpdateScore(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().incrementScore(key, value, score);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean zsBatchSet(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        try {
            redisTemplate.opsForZSet().add(key, tuples);
            return true;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long reverseRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Double score(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return 0D;
        }
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> rangeWithScores(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Set<String> batchGetKeys(String prefix) {
        return prefix == null ? null : redisTemplate.keys(prefix);
    }

    @Override
    public void batchDel(String prefix) {
        Set<String> keys = batchGetKeys(prefix);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public RecordId streamAdd(StringRecord record) {
        try {
            return redisTemplate.opsForStream().add(record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RecordId streamAdd(Record<String, ?> record) {
        try {
            return redisTemplate.opsForStream().add(record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RecordId streamAdd(String stream, Map<String, ?> value) {
        try {
            return redisTemplate.opsForStream().add(stream, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<List<ObjectRecord<String, T>>> streamRead(Class<T> clazz, String stream, RecordId recordId) {
        try {
            return Optional.of(redisTemplate.opsForStream().read(clazz, StreamOffset.create(stream, ReadOffset.from(recordId))));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Long streamAck(String group, Record<String, ?> record) {
        try {
            return redisTemplate.opsForStream().acknowledge(group, record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Long streamAck(String stream, String group, String... recordIds) {
        try {
            return redisTemplate.opsForStream().acknowledge(stream, group, recordIds);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
