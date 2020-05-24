--
-- Copyright 2020 本文件属于 xBoson 项目, 该项目由 J.yanming 维护,
-- 本文件和项目的全部权利由 [荆彦铭] 和 [王圣波] 个人所有, 如有对本文件或项目的任何修改,
-- 必须通知权利人; 该项目非开源项目, 任何将本文件和项目未经权利人同意而发行给第三方
-- 的行为都属于侵权行为, 权利人有权对侵权的个人和企业进行索赔; 未经其他合同约束而
-- 由本项目(程序)引起的计算机软件/硬件问题, 本项目权利人不负任何责任, 切不对此做任何承诺.
--
-- 文件创建日期: 20-5-24 上午8:13
-- 原始文件路径: D:/javaee-project/xBoson/src/com/xboson/fs/redis/find_text.lua
-- 授权说明版本: 1.1
--
-- [ J.yanming - Q.412475540 ]
--

--
-- 在指定文件中搜索字符串, 找到返回 true
--

local key           = KEYS[1];
local caseSensitive = ARGV[1] == "true";
local filename      = ARGV[2];
local what          = ARGV[3];


local function case_insensitive_pattern(pattern)
    --
    -- find an optional '%' (group 1) followed by any character (group 2)
    --
    local p = pattern:gsub("(%%?)(.)", function(percent, letter)
        if percent ~= "" or not letter:match("%a") then
            --
            -- if the '%' matched, or `letter` is not a letter, return "as is"
            --
            return percent .. letter
        else
            --
            -- else, return a case-insensitive character class of the matched letter
            --
            return string.format("[%s%s]", letter:lower(), letter:upper())
        end
    end)

    return p
end


if (caseSensitive == false) then
    what = case_insensitive_pattern(what);
end

local filecontent = redis.call("hget", key, filename);
if (filecontent == false) then
    return nil;
end
--print(filecontent, key, filename);
return string.find(filecontent, what, 1, caseSensitive);