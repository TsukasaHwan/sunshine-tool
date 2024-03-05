local count = redis.call('ZCOUNT', KEYS[1], ARGV[1], ARGV[2])
if count < tonumber(ARGV[3]) then
    redis.call('ZADD', KEYS[1], ARGV[4], ARGV[4])
    return 1
else
    return 0
end