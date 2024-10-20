package com.example.aerospikedemo.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.WritePolicy;

@Component
public class AerospikeUtil {

	@Autowired
	private AerospikeClient aerospikeClient;

	private static final String NAMESPACE = "test";

	public void addUpdateCache(String setName, String keyValue, Map<String, Object> dataMap) {
		addUpdateCache(null, setName, keyValue, dataMap);
	}

	public void addUpdateCache(WritePolicy writePolicy, String setName, String keyValue, Map<String, Object> dataMap) {
		Key key = new Key(NAMESPACE, setName, keyValue);
		Bin[] bins = dataMap.entrySet().stream().map(entry -> new Bin(entry.getKey(), entry.getValue()))
				.toArray(Bin[]::new);
		aerospikeClient.put(getValidWritePolicy(writePolicy), key, bins);
	}

	public void addUpdateCache(String setName, String keyValue, Object object) {
		addUpdateCache(null, setName, keyValue, object);
	}

	public void addUpdateCache(WritePolicy writePolicy, String setName, String keyValue, Object object) {
		Key key = new Key(NAMESPACE, setName, keyValue);
		Bin[] bins = convertObjectToBins(object);
		aerospikeClient.put(getValidWritePolicy(writePolicy), key, bins);
	}

	public void addUpdateCache(String setName, String keyValue, int ttl, Object object) {
		WritePolicy writePolicy = new WritePolicy();
		writePolicy.expiration = ttl;
		addUpdateCache(writePolicy, setName, keyValue, object);
	}

	public <T extends Serializable> T getRecord(String set, String primaryKey, Class<T> typeParameterClass) {
		Key key = new Key(NAMESPACE, set, primaryKey);
		Record record = aerospikeClient.get(null, key);
		return record != null ? convertRecordToObject(record, typeParameterClass) : null;
	}

	public boolean checkKeyExists(String set, String key) {
		Key aerospikeKey = new Key(NAMESPACE, set, key);
		Record record = aerospikeClient.get(null, aerospikeKey);
		return record != null;
	}

	public boolean deleteRecord(WritePolicy writePolicy, String set, String primaryKey) {
		Key key = new Key(NAMESPACE, set, primaryKey);
		return aerospikeClient.delete(getValidWritePolicy(writePolicy), key);
	}

	public <T extends Serializable> List<T> getRecords(String set, List<String> primaryKeys,
			Class<T> typeParameterClass) {
		return primaryKeys.stream().map(key -> getRecord(set, key, typeParameterClass)).collect(Collectors.toList());
	}

	public Map<String, Object> getRecord(String set, String primaryKey) {
		Key key = new Key(NAMESPACE, set, primaryKey);
		Record record = aerospikeClient.get(null, key);
		return record != null ? record.bins : null;
	}

	private WritePolicy getValidWritePolicy(WritePolicy writePolicy) {
		return writePolicy != null ? writePolicy : aerospikeClient.writePolicyDefault;
	}

	private Bin[] convertObjectToBins(Object object) {
		Map<String, Object> map = CommonUtils.getObjectMapper().convertValue(object, Map.class);
		return map.entrySet().stream().map(entry -> new Bin(entry.getKey(), entry.getValue())).toArray(Bin[]::new);
	}

	private <T> T convertRecordToObject(Record record, Class<T> typeParameterClass) {
		return CommonUtils.getObjectMapper().convertValue(record.bins, typeParameterClass);
	}
}
