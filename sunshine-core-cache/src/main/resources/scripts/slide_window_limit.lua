--KEYS[1]: 限流 key
--ARGV[1]: 时间戳 - 时间窗口
--ARGV[2]: 当前时间戳（作为score）
--ARGV[3]: 阈值
--ARGV[4]: score 对应的唯一value
--ARGV[5]: 失效时间
-- 1. 移除时间窗口之前的数据
redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])
-- 2. 统计当前元素数量
local res = redis.call('zcard', KEYS[1])
-- 3. 是否超过阈值
if (res == nil) or (res < tonumber(ARGV[3])) then
    redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])
    -- 设置过期时间，防止冷用户持续占用内存,
    -- 过期时间应该是时间窗口长度+失效时间
    -- redis.call("expire", KEYS[1], ARGV[5]);
    return 1
else
    return 0
end