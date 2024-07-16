local limit = tonumber(ARGV[1])
local expire_time = tonumber(ARGV[2])

local current_count = redis.call('GET', KEYS[1])

if not current_count then
    redis.call('SET', KEYS[1], 1)
    redis.call('EXPIRE', KEYS[1], expire_time)
    return 1
else
    current_count = tonumber(current_count)

    if current_count >= limit then
        return 0
    else
        local new_count = redis.call('INCR', KEYS[1])
        return new_count
    end
end