package com.nhattung.wogo.utils;

public class RedisKeyUtil {
    private static final String JOB_DETAIL_KEY = "job:%s";
    private static final String JOB_LIST_KEY = "service:%d:jobs";
    private static final String JOB_LOCK_KEY = "lock:job:%s";
    private static final String WORKER_LOCATION_KEY = "worker:locations:%s";
    private static final String JOB_LIST_DETAIL_BY_USER_KEY = "job:list:detail:user:%d";
    public static String jobDetailKey(String code) {
        return String.format(JOB_DETAIL_KEY, code);
    }

    public static String jobListByServiceKey(Long serviceId) {
        return String.format(JOB_LIST_KEY, serviceId);
    }

    public static String jobListDetailByUserKey(Long userId) {
        return String.format(JOB_LIST_DETAIL_BY_USER_KEY, userId);
    }

    public static String jobLockKey(String code) {
        return String.format(JOB_LOCK_KEY, code);
    }

    public static String realtimeLocationKey(String bookingCode) {
        return String.format(WORKER_LOCATION_KEY, bookingCode);
    }
}