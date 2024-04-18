package com.vox.usercenter.once;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VoxUserInfoListener implements ReadListener<VoxUserInfo> {

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    data 就是每一次读取的值
     * @param context 和当前读取的文件相关，例如记录当前读取的是第几行
     */
    @Override
    public void invoke(VoxUserInfo data, AnalysisContext context) {

    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        log.info("所有数据解析完成！");
    }

}