local limit = tonumber(ARGV[1])
local expire_time = ARGV[2]
local result = redis.call('SETNX',KEYS[1],1);

if result == 1 then
    redis.call('expire',KEYS[1],expire_time)
    return 1
else
    if tonumber(redis.call('GET', KEYS[1])) >= limit then
        return 0
    else
        limit = redis.call('incr', KEYS[1])
        return limit
    end
end;