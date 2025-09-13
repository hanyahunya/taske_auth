package com.hanyahunya.auth.redis;

public interface RedisService {
    /**
     * キーに値を保存（有効期間なし）
     * @param key Redisキー
     * @param value 保存する値（オブジェクト）
     */
    void save(String key, Object value);

    /**
     * キーに値を保存（有効期間設定）
     * @param key Redisキー
     * @param value 保存する値（オブジェクト）
     * @param expirationInSeconds 有効期間（秒単位）
     */
    void save(String key, Object value, long expirationInSeconds);

    /**
     * キーに対応する値を取得
     * @param key Redisキー
     * @param clazz 変換するクラスタイプ
     * @return 変換されたオブジェクト（値がない場合や、エラー発生時はnull）
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * キーに対応する値を削除
     * @param key Redisキー
     */
    void delete(String key);
}