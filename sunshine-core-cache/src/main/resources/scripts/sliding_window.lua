local current_timestamp = tonumber(ARGV[1])
local window_start_timestamp = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local window_key = KEYS[1]
local expired_data_count = redis.call('ZREMRANGEBYSCORE', window_key, '-inf', window_start_timestamp)
local window_data = redis.call('ZRANGE', window_key, 0, -1)
local window_size = table.getn(window_data)
if window_size >= limit then
    return 0
else
    redis.call('ZADD', window_key, current_timestamp, current_timestamp)
    return 1
end