-- 通过 contentid(1) 返回 appid/modid/apiid
SELECT
    ap.appid `app`, ap.moduleid `mod`, ap.apiid `api`
FROM
    sys_api_content ac,
    sys_apis ap
WHERE
    ac.contentid = ?
        AND ac.contentid = ap.contentid
        AND ac.content IS NOT NULL
