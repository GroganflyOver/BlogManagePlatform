package frodez.cache.vm.facade;

public interface ICacheService<K, V> {

	/**
	 * 判断key是否存在于缓存
	 * @author Frodez
	 * @date 2019-02-27
	 */
	boolean existKey(K key);

	/**
	 * 判断value是否存在于缓存
	 * @author Frodez
	 * @date 2019-02-27
	 */
	boolean existValue(V value);

	/**
	 * 存储key和value于缓存
	 * @author Frodez
	 * @date 2019-02-27
	 */
	void save(K key, V value);

	/**
	 * 通过key获取缓存
	 * @author Frodez
	 * @date 2019-02-27
	 */
	V get(K key);

	/**
	 * 根据key删除对应缓存
	 * @author Frodez
	 * @date 2019-02-27
	 */
	void remove(K key);

}
