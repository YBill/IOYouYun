package com.ioyouyun.group.biz;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public interface GroupRequest {

    /**
     * 获取用户群列表
     * @param uid 用户Id
     * @param cat1 群类型,0 为临时群, 1为私密群, 2为公开群, 3为聊天室  可以是多种类型的组合,以","分割
     * @param cat2 群类型：0 表示群组, 1 表示群联盟  可以是多种类型的组合,以","分割
     * @param listener
     */
    void getGroupList(long uid, String cat1, String cat2, OnGroupListener listener);

    /**
     * 创建群组
     * @param name 群名称
     * @param intra 群简介
     * @param cat1 群类型,0 为临时群,1为私密群,2为公开群,3为聊天室
     * @param cat2 群类型：0 表示群组,1 表示群联盟
     * @param listener
     */
    void createGroup(String name, String intra, int cat1, int cat2, OnGroupListener listener);

    /**
     * 获取群信息
     * @param gid
     * @param listener
     */
    void getGroupInfo(long gid, OnGroupListener listener);

    /**
     * 获取群成员
     * @param gid 群Id
     * @param role 按角色过滤,默认所有成员,1.普通成员 2.vip用户 3.管理员 4.群主
     * @param count 获取成员个数，默认所有
     * @param listener
     */
    void getGroupMembers(long gid, int role, int count, OnGroupListener listener);

    /**
     * 退群
     * @param gid
     * @param listener
     */
    void exitGroup(long gid, OnGroupListener listener);

    /**
     * 解散群
     * @param groupId
     * @param listener
     */
    void deleteGroup(String groupId, OnGroupListener listener);

    /**
     * 添加群成员
     * @param gid
     * @param uids
     * @param listener
     */
    void addGroupUsers(long gid, String uids, OnGroupListener listener);

    /**
     * 申请入群
     * @param gid
     * @param intra
     * @param listener
     */
    void applyAddGroup(long gid, String intra, OnGroupListener listener);

}
