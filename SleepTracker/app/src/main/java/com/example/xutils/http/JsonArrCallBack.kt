package com.example.xutils.http

import com.example.tools.JsonTools
import org.json.JSONArray

open class JsonArrCallBack : XCallBack<JSONArray> {

    override fun onSuccess(result: JSONArray) {}

    override fun onError(throwable: Throwable, b: Boolean) {}

    override fun onCancelled(e: Exception) {}

    override fun onFinished() {}

    override fun getResult(result: String): JSONArray {
        return JsonTools.generateJsonArr(result);
    }
}