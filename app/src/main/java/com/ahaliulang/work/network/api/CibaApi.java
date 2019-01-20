package com.ahaliulang.work.network.api;

import com.ahaliulang.work.bean.CibaBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CibaApi {
    /**
     * date	//标准化日期格式 如：2013-05-06， 如：http://open.iciba.com/dsapi/?date=2013-05-03
     * 如果 date为空 则默认取当日的，当日为空 取前一日的
     * type(可选)	// last 和 next 你懂的，以date日期为准的，last返回前一天的，next返回后一天的
     */
    @GET("dsapi/")
    Observable<CibaBean> ciba(@Query("date")String date);

}
