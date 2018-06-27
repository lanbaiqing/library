package com.zhy.http.okhttps.builder;

import com.zhy.http.okhttps.OkHttpUtils;
import com.zhy.http.okhttps.request.OtherRequest;
import com.zhy.http.okhttps.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
